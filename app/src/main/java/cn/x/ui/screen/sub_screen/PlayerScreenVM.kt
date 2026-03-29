package cn.x.ui.screen.sub_screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.service.PlayMode
import cn.x.service.PlayerController
import cn.x.util.AudioMetadataUtil
import cn.x.util.LyricLine
import cn.x.util.LyricUtil
import cn.x.util.getFilePath
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PlayerScreenVM @Inject constructor(
    val playerController: PlayerController // 注入单例控制器
) : ViewModel() {

    private val _lyrics = MutableStateFlow(listOf(LyricLine(0L, "no lyric")))
    val lyrics = _lyrics.asStateFlow()


    init {
        viewModelScope.launch(Dispatchers.IO){
            // todo 应是实时监听触发,不然即使切歌也不会更新歌词
            getCurrentSongLyric()
        }
    }

    private fun getCurrentSongLyric() {
        val filepath = playerController.currentSong.value?.mediaMetadata?.getFilePath() ?: ""
        val metadata = AudioMetadataUtil.readMetadata(filepath)
        val lyrics = metadata["lyrics"]
//        Log.d("PlayerScreenVM", "getCurrentSongLyric: ${lyrics}")

        if (!lyrics.isNullOrBlank()) {
            val line = LyricUtil.parseLyric(lyrics)
            _lyrics.value = line!!
        }

    }

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
        playerController.seekTo(msec)
    }

    fun togglePlayMode() {
        val currentMode = playerController.playMode
        val newMode = PlayMode.valueOf((currentMode.value.value + 1) % 3)
        playerController.setPlayMode(newMode)
    }

}