package cn.x.ui.screen.sub_screen


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import cn.x.data.MusicDatabase
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListEntity
import cn.x.data.db.SongListWithSongEntity
import cn.x.service.PlayerController
import cn.x.util.Constants
import cn.x.util.toMediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SongsScreenVM @Inject constructor(
    private val db: MusicDatabase,
    val playerController: PlayerController,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val EMPTY = SongListEntity(0L, "", "", 0, "", -1)
    private val _songList = MutableStateFlow(EMPTY)
    val songList = _songList.asStateFlow()

    private val _songs = MutableStateFlow<List<SongEntity>>(emptyList())
    val songs: StateFlow<List<SongEntity>> = _songs.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    private val songListId: Long = savedStateHandle[Constants.SongListId] ?: 0L

    init {

        // todo 在添加歌曲后返回该页面,这种方式不会重新加载歌曲
        // 先用refresh吧
        loadData()
    }

    fun loadData() {
        // 模拟加载数据
        viewModelScope.launch {
            val songListWithSongs = withContext(Dispatchers.IO) {
                // 查询歌单 查询歌曲 独立进行 不然后续不好分页
                // 先不分页了
                db.SongListDao().getSongsBySongListId(songListId)
            }
            _songs.value = songListWithSongs?.songs ?: emptyList() // 在 Main 线程更新
            _songList.value = songListWithSongs?.songList ?: EMPTY
        }
    }

    fun toggleSelectionMode() {
        val newMode = !_isSelectionMode.value
        _isSelectionMode.value = newMode
        if (!newMode) {
            _selectedIds.value = emptySet()
        }
    }

    fun toggleSelection(id: String) {
        val current = _selectedIds.value.toMutableSet()
        if (id in current) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _selectedIds.value = current
    }

    fun allSelection() {
//        _selectedIds.value = emptySet()
        _selectedIds.value = _songs.value.map { it.uniqueId }.toMutableSet()
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }


    fun play(song: MediaItem) {
        playerController.replaceAll(_songs.value.map { it.toMediaItem() }, song)
    }

    fun remove() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val removeList = _selectedIds.value.map {
                    SongListWithSongEntity(
                        songlistId = songListId,
                        songId = it
                    )
                }
                db.SongListDao().deleteSongFromSongList(removeList)
            }
            _songs.value = _songs.value.filter { it.uniqueId !in _selectedIds.value }
            _selectedIds.value = emptySet()
        }
    }
}