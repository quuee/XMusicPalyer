package cn.x.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.db.MusicDatabase
import cn.x.data.db.SongEntity
import cn.x.service.PlayerController
import cn.x.util.toLocalMediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalSongScreenVM @Inject constructor(
    private val db: MusicDatabase,
    private val playerController: PlayerController
) : ViewModel() {

    private val tag = "LocalSongScreenVM"
    private val _songList = MutableStateFlow<List<SongEntity>>(emptyList())
    val songList: StateFlow<List<SongEntity>> = _songList.asStateFlow()

    init {
        Log.d(tag, "init")
        // 在 viewModelScope 内启动一个协程
        viewModelScope.launch(Dispatchers.IO) { // 使用 IO 调度器以确保在后台线程运行
            try {
                val songs = db.SongsDao().queryAll() // 这行现在在后台线程执行
                _songList.value = songs
                Log.d(tag, "songs: ${songs.first()}")
                Log.d(tag, "songs: ${songs.size}")
            } catch (e: Exception) {
                Log.e(tag, "Error loading songs from database", e)
                // 可以在这里设置一个错误状态或空列表
            }
        }
    }

    fun play(song: SongEntity) {
        playerController.addAndPlay(song.toLocalMediaItem())
    }

}