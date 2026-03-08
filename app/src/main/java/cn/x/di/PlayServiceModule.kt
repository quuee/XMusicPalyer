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

    fun Application.playerController(): PlayerController {
        return EntryPointAccessors.fromApplication(this, PlayerControllerEntryPoint::class.java)
            .playerController()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PlayerControllerEntryPoint {
        fun playerController(): PlayerController
    }
}