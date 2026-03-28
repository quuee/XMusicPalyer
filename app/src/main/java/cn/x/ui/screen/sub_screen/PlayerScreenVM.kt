package cn.x.ui.screen.sub_screen

import android.util.Log
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.times

@HiltViewModel
class PlayerScreenVM @Inject constructor(
    val playerController: PlayerController // 注入单例控制器
) : ViewModel() {

    private val _lyrics = MutableStateFlow<List<LyricLine>>(listOf(LyricLine(0L, "no lyric")))
    val lyrics = _lyrics.asStateFlow()

    private val _isVisible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

    private val _screenHeight = MutableStateFlow(0f)
    val screenHeight: StateFlow<Float> = _screenHeight.asStateFlow()

    private val _offsetY = MutableStateFlow(0f)
    val offsetY: StateFlow<Float> = _offsetY.asStateFlow()

    // 进度计算
    val progress: StateFlow<Float> = combine(_offsetY, _screenHeight) { offset, height ->
        if (height > 0) (1f - (offset / height)).coerceIn(0f, 1f) else 0f
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0f
    )

    private var closeThreshold = 0.3f
    private var openThreshold = 0.7f

    init {
        viewModelScope.launch(Dispatchers.IO){
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

    fun updateOffset(offset: Float) {
        _offsetY.value = offset.coerceIn(0f, _screenHeight.value)
    }

    fun openFullPlayer() {
        _isVisible.value = true
    }

    fun closeFullPlayer() {
        _isVisible.value = false
    }

    fun toggleFullPlayer() {
        _isVisible.value = !_isVisible.value
    }

    fun setScreenHeight(height: Float) {
        _screenHeight.value = height
        _offsetY.value = if (_isVisible.value) 0f else height
    }

    fun shouldCloseOnDrag(currentOffset: Float, velocity: Float): Boolean {
        val screenH = _screenHeight.value
        return if (kotlin.math.abs(velocity) > 1000f) {
            velocity > 0
        } else {
            currentOffset > screenH * closeThreshold
        }
    }

    fun shouldOpenOnDrag(currentOffset: Float, velocity: Float): Boolean {
        val screenH = _screenHeight.value
        return if (kotlin.math.abs(velocity) > 1000f) {
            velocity < 0
        } else {
            currentOffset < screenH * openThreshold
        }
    }

    fun setThresholds(close: Float = 0.3f, open: Float = 0.7f) {
        closeThreshold = close.coerceIn(0f, 1f)
        openThreshold = open.coerceIn(0f, 1f)
    }

}