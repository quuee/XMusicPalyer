package cn.x.ui.screen.sub_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.MusicDatabase
import cn.x.data.dao.SongDao
import cn.x.data.dao.SongListDao
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListWithSongEntity
import cn.x.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddSelectSongScreenVM(
    private val songListDao: SongListDao,
    private val songDao: SongDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 非歌单歌曲
    private val _unselectSongs = MutableStateFlow<List<SongEntity>>(emptyList())
    val unselectSongs: StateFlow<List<SongEntity>> = _unselectSongs

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds

    private val _searchWord = MutableStateFlow<String?>(null)
    val searchWord = _searchWord.asStateFlow()

    private val songListId: Long = savedStateHandle[Constants.SongListId] ?: 0L

    init {
        loadData()
    }

    fun loadData() {
        // 进入歌单页面 加载数据
        viewModelScope.launch {
            val songListWithSongs = songListDao.getSongsBySongListId(songListId)
            val songs = songListWithSongs?.songs ?: emptyList()

            val selectedSongIds = songs.map { it.uniqueId }.toSet()

            songDao.queryAll().collect { items ->
                // 过滤已有歌曲
                val unselectedSongs = items.filter { it.uniqueId !in selectedSongIds }
                _unselectSongs.value = unselectedSongs
            }
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

    fun addSelectToSongList(songListId: Long) {
        val list =
            _selectedIds.value.map { SongListWithSongEntity(songlistId = songListId, songId = it) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                songListDao.insertSongsToSongList(list)
                val songList = songListDao.getSongList(songListId)
                val newNongList = songList.copy(count = songList.count + list.size)
                songListDao.updateSongList(newNongList)
            }

        }
    }

    fun changeSearchWord(word: String?) {
        _searchWord.value = word

        viewModelScope.launch {
            val songListWithSongs = songListDao.getSongsBySongListId(songListId)
            val songs = songListWithSongs?.songs ?: emptyList()

            val selectedSongIds = songs.map { it.uniqueId }.toSet()

            songDao.queryLike(word).collect { items ->
                // 过滤已有歌曲
                val unselectedSongs = items.filter { it.uniqueId !in selectedSongIds }
                _unselectSongs.value = unselectedSongs
            }
        }
    }
}