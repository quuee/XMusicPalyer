package cn.x.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import cn.x.data.db.MusicDatabase
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListEntity
import cn.x.service.PlayerController
import cn.x.util.toMediaItem
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
    private val _songs = MutableStateFlow<List<SongEntity>>(emptyList())
    val songs: StateFlow<List<SongEntity>> = _songs.asStateFlow()

    private val _songLists = MutableStateFlow<List<SongListEntity>>(emptyList())
    val songLists: StateFlow<List<SongListEntity>> = _songLists.asStateFlow()

    private val _selectSong = MutableStateFlow<SongEntity?>(null)
    val selectSong = _selectSong.asStateFlow()

    private val _bottomSheetVisible = MutableStateFlow(false)
    val bottomSheetVisible = _bottomSheetVisible.asStateFlow()

    private val _songListDialogVisible = MutableStateFlow(false)
    val songListDialogVisible = _songListDialogVisible.asStateFlow()

    init {
        Log.d(tag, "init")
        // 在 viewModelScope 内启动一个协程
        viewModelScope.launch(Dispatchers.IO) { // 使用 IO 调度器以确保在后台线程运行
            try {
                _songs.value = db.SongDao().queryAll() // 这行现在在后台线程执行, todo 分页加载
                _songLists.value = db.SongListDao().getAllSongLists()
            } catch (e: Exception) {
                Log.e(tag, "Error loading songs from database", e)
                // 可以在这里设置一个错误状态或空列表
            }
        }
    }

    fun play(song: MediaItem) {
        playerController.replaceAll(_songs.value.map { it.toMediaItem() }, song)
    }

    fun showBottomSheet(song: SongEntity) {
        _bottomSheetVisible.value = true
        _selectSong.value = song
    }

    fun hideBottomSheet() {
        _bottomSheetVisible.value = false
        _selectSong.value = null
    }

    fun showSongListDialog(){
        _songListDialogVisible.value = true
        // todo 打开时才加载歌单
    }

    fun hideSongListDialog(){
        _songListDialogVisible.value = false
    }

}