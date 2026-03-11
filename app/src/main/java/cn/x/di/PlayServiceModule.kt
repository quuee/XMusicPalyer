package cn.x.di

import android.app.Application
import androidx.media3.session.MediaController
import cn.x.data.db.MusicDatabase
import cn.x.service.PlayerController
import cn.x.service.PlayerControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Module
@InstallIn(SingletonComponent::class)
object PlayServiceModule {
    private var player: MediaController? = null
    private var playerController: PlayerController? = null

    private val _isPlayerReady = MutableStateFlow(false)
    val isPlayerReady: StateFlow<Boolean> = _isPlayerReady.asStateFlow()

    fun setPlayer(player: MediaController) {
        this.player = player
        _isPlayerReady.value = true
    }

    @Provides
    fun providerPlayerController(db: MusicDatabase): PlayerController {
        return playerController ?: run {
            val player = player ?: throw IllegalStateException("Player not prepared!")
            PlayerControllerImpl(player, db).also {
                playerController = it
            }
        }
    }


    // region
    /**
     * 在没有使用 Hilt 注入（比如在普通 Kotlin 类、工具函数、或 Compose 作用域外）的情况下，手动从 Application 中获取由 Hilt 提供的 PlayerController 实例
     */
    fun Application.playerController(): PlayerController {
        //EntryPointAccessors.fromApplication(...)	从 Application 上下文获取该入口点实例
        //Application.playerController() 扩展函数	提供一个便捷方法，让任何持有 Application 的地方都能拿到 PlayerController
        return EntryPointAccessors.fromApplication(this, PlayerControllerEntryPoint::class.java)
            .playerController()
    }

    /**
     * 只有在 Hilt 无法自动注入的地方（如普通工具类、BroadcastReceiver、未加 @AndroidEntryPoint 的 Service），才需要 EntryPoint
     * @EntryPoint 告诉 Hilt：这个接口是用来“手动访问”依赖注入容器的。
     * @InstallIn(SingletonComponent::class) 表示这个入口点绑定到 整个应用的单例作用域（和 @Singleton 组件一致）
     */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PlayerControllerEntryPoint {
        fun playerController(): PlayerController
    }
    // endregion
}