package cn.x

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import cn.x.di.appModule
import cn.x.di.databaseModule
import cn.x.di.utilModule
import cn.x.di.viewModelModule
import cn.x.service.MusicPlaybackService
import cn.x.route.NavigationGraph
import cn.x.ui.theme.AppThemeMode
import cn.x.ui.theme.XMusicPlayerTheme
import cn.x.util.SPUtil
import cn.x.util.ToastUtil
import kotlinx.coroutines.CompletableDeferred
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.mp.KoinPlatform.getKoin


class XMusicApplication : Application() {

    val mediaControllerReady = CompletableDeferred<Unit>()

    override fun onCreate() {
        super.onCreate()

        SPUtil.init(this)
        ToastUtil.init(this)

        // 初始化 Koin DI
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@XMusicApplication)
            modules(
                databaseModule,     // Room 配置
                appModule,
                utilModule,
                viewModelModule,
            )
        }
        initMediaController()
    }

    private fun initMediaController() {
        val sessionToken = SessionToken(
            this,
            ComponentName(this, MusicPlaybackService::class.java)
        )
        val future = MediaController.Builder(this, sessionToken).buildAsync()

        future.addListener({
            try {
                val controller = future.get()

                // ✅ Koin 4.x 动态声明：将已就绪的实例注入容器
                getKoin().declare(controller)

                // ✅ 通知 UI 可以展示了
                mediaControllerReady.complete(Unit)

                Log.d("MyMusicApp", "MediaController ready")
            } catch (e: Exception) {
                Log.e("MyMusicApp", "MediaController init failed", e)
                // 即使失败也要 complete，否则 UI 永远卡住
                // 可根据业务需求改为 completeExceptionally
                mediaControllerReady.complete(Unit)
            }
        }, ContextCompat.getMainExecutor(this))
    }
}

@Composable
fun SplashScreen(onReady: () -> Unit) {
    val app = LocalContext.current.applicationContext as XMusicApplication

    LaunchedEffect(Unit) {
        // ✅ 挂起等待，不会阻塞主线程
        app.mediaControllerReady.await()
        onReady()
    }

    // 等待期间展示的 UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // 你的 Logo / 加载动画
        Image(
            modifier = Modifier.size(72.dp),
            painter = painterResource(R.drawable.logo),
            contentDescription = null
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun XMusicApplicationApp(
) {
    var isReady by remember { mutableStateOf(false) }

    XMusicPlayerTheme(
        themeMode = AppThemeMode.SYSTEM
    ) {
        if (!isReady) {
            SplashScreen(onReady = { isReady = true })
        } else {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
            ) {
                NavigationGraph()
            }
        }
    }

}

