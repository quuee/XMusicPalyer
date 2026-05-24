package cn.x.ui.screen.sub_screen


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import cn.x.data.dao.SongListDao
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListEntity
import cn.x.data.db.SongListWithSongEntity
import cn.x.service.PlayerController
import cn.x.util.Constants
import cn.x.util.toMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SongsScreenVM (
    private val songListDao: SongListDao,
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


    fun loadData() {
        // 加载数据
        viewModelScope.launch {
            val songListWithSongs = songListDao.getSongsBySongListId(songListId)
            _songs.value = songListWithSongs?.songs ?: emptyList()
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
                songListDao.deleteSongFromSongList(removeList)

                val songList = songListDao.getSongList(songListId)
                val newNongList = songList.copy(count = songList.count - removeList.size)
                songListDao.updateSongList(newNongList)
            }
            _songs.value = _songs.value.filter { it.uniqueId !in _selectedIds.value }
            _selectedIds.value = emptySet()
        }
    }

    fun getCurrentSongIndex(): Int {
        val index =
            _songs.value.indexOfFirst { it.uniqueId == playerController.currentSong.value?.mediaId }
        return index

    }
}