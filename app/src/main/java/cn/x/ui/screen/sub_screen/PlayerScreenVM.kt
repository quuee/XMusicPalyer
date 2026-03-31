package cn.x.ui.screen.sub_screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
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
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class PlayerScreenVM @Inject constructor(
    val playerController: PlayerController // 注入单例控制器
) : ViewModel() {

    private val _lyrics = MutableStateFlow(listOf(LyricLine(0L, "no lyric")))
    val lyrics = _lyrics.asStateFlow()


    init {
        viewModelScope.launch {
            playerController.currentSong.collect { song ->
                song?.let { mediaItem->
                    getCurrentSongLyric(mediaItem)
                }
            }
        }
    }

    private fun getCurrentSongLyric(mediaItem: MediaItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val filepath = mediaItem.mediaMetadata.getFilePath()
            if (filepath.isBlank()) {
                _lyrics.value = listOf(LyricLine(0L, "no lyric"))
                return@launch
            }

            try {
                // 在 IO 线程执行文件读取操作
                val metadata = AudioMetadataUtil.readMetadata(filepath)
                val lyrics = metadata["lyrics"]

                // 在主线程更新 UI 状态
                withContext(Dispatchers.Main) {
                    if (!lyrics.isNullOrBlank()) {
                        val parsedLines = LyricUtil.parseLyric(lyrics)
                        _lyrics.value = parsedLines ?: listOf(LyricLine(0L, "no lyric"))
                    } else {
                        _lyrics.value = listOf(LyricLine(0L, "no lyric"))
                    }
                }
            } catch (e: Exception) {
                // 处理异常情况
                e.printStackTrace()
                _lyrics.value = listOf(LyricLine(0L, "load lyric failed"))
            }
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

    fun play(songUniqueId:String){
        playerController.play(songUniqueId)
    }

}