package cn.x.ui.screen

import androidx.lifecycle.ViewModel
import cn.x.data.db.SongEntity
import cn.x.service.PlayerController
import cn.x.util.toLocalMediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerScreenVM @Inject constructor(
    val playerController: PlayerController // 注入单例控制器
) : ViewModel() {


    // UI 事件委托给 Controller
    fun togglePlayPause() {
        val m = SongEntity(
            type = 0,
            songId = 1,
            title = "wanfeng",
            artist = "aliyue",
            uri = "android.resource://cn.x/raw/test"
        ).toLocalMediaItem()
        playerController.addAndPlay(m)
    }

    fun next() {
        playerController.next()
    }

    fun prev() {
        playerController.prev()
    }

    fun seekTo(progress: Float) {
        // progress 是 0.0 - 1.0
        val duration = playerController.mediaController.duration
        if (duration > 0) {
            val positionMs = (progress * duration).toLong()
            playerController.seekTo(positionMs.toInt())
        }
    }

    fun togglePlayMode() {

        //playerController.setPlayMode(newMode)
    }

    override fun onCleared() {
        super.onCleared()
        // 通常不需要在这里 stop()，除非你想在退出应用时停止播放。
        // 一般音乐播放器会在后台 Service 中运行，ViewModel 销毁不影响播放。
    }

}