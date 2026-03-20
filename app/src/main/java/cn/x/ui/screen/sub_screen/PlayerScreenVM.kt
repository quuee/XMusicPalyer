package cn.x.ui.screen.sub_screen

import androidx.lifecycle.ViewModel
import cn.x.service.PlayMode
import cn.x.service.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerScreenVM @Inject constructor(
    val playerController: PlayerController // 注入单例控制器
) : ViewModel() {


    // UI 事件委托给 Controller
    fun togglePlayPause() {
        playerController.playPause()
    }

    fun next() {
        playerController.next()
    }

    fun prev() {
        playerController.prev()
    }

    fun seekTo(msec: Long) {
        // progress 是 0.0 - 1.0
//        val duration = playerController.mediaController.duration
//        if (duration > 0) {
//            val positionMs = (progress * duration).toLong()
//            playerController.seekTo(positionMs)
//        }
        playerController.seekTo(msec)
    }

    fun togglePlayMode() {
        val currentMode = playerController.playMode
        val newMode = PlayMode.valueOf((currentMode.value.value + 1) % 3)
        playerController.setPlayMode(newMode)
    }

    override fun onCleared() {
        super.onCleared()
        // 通常不需要在这里 stop()，除非你想在退出应用时停止播放。
        // 一般音乐播放器会在后台 Service 中运行，ViewModel 销毁不影响播放。
    }

}