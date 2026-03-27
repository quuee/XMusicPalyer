package cn.x

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.compose.rememberNavController
import cn.x.di.PlayServiceModule
import cn.x.di.SettingModule
import cn.x.service.MusicPlaybackService
import cn.x.ui.NavigationGraph
import cn.x.ui.Screens
import cn.x.ui.theme.XMusicPlayerTheme
import cn.x.util.SPUtil
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class XMusicApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        SPUtil.init(this)

        Log.d("XMusicApplication", "onCreate: init mediaController")
        val sessionToken =
            SessionToken(this, ComponentName(this, MusicPlaybackService::class.java))
        val mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            val player = mediaControllerFuture.get()
            PlayServiceModule.setPlayer(player)
//            WidgetRepository.init(this) // 桌面小组件
        }, MoreExecutors.directExecutor())
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun XMusicApplicationApp(
) {
    val isReady by PlayServiceModule.isPlayerReady.collectAsState()
    val themeMode by SettingModule.themeMode.collectAsState()
    XMusicPlayerTheme(
        themeMode = themeMode
    ) {
        if (isReady) {
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) {
                val navHostController = rememberNavController()
                NavigationGraph(
                    navHostController = navHostController,
                    startDistance = Screens.Home.route
                )
            }

        } else {
            LoadingScreen()
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // 背景色
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("正在启动音乐服务...", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
