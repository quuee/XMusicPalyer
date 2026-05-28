package cn.x.ui.screen.sub_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.dao.SongDao
import cn.x.data.dao.SongListDao
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListWithSongEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class AddSelectSongIntent {
    data class LoadData(val songListId: Long) : AddSelectSongIntent()
    data class ToggleSelection(val songId: String) : AddSelectSongIntent()
    data class AddSelectToSongList(val songListId: Long) : AddSelectSongIntent()
    data class ChangeSearchWord(val songListId: Long, val word: String?) : AddSelectSongIntent()
}

sealed class AddSelectSongEffect {
    data class ShowMessage(val message: String) : AddSelectSongEffect()
    data object Back : AddSelectSongEffect()
}

class AddSelectSongScreenVM(
    private val songListDao: SongListDao,
    private val songDao: SongDao,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _error = MutableStateFlow(null)
    val error = _error.asStateFlow()

    // 私有副作用通道 (Channel 用于处理一次性事件)
    private val _effect = Channel<AddSelectSongEffect>(Channel.BUFFERED)
    val effect: Flow<AddSelectSongEffect> = _effect.receiveAsFlow()

    // 非歌单歌曲
    private val _unselectSongs = MutableStateFlow<List<SongEntity>>(emptyList())
    val unselectSongs: StateFlow<List<SongEntity>> = _unselectSongs

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds

    private val _searchWord = MutableStateFlow<String?>(null)
    val searchWord = _searchWord.asStateFlow()

    fun handleIntent(intent: AddSelectSongIntent) {
        when (intent) {
            is AddSelectSongIntent.LoadData -> loadData(intent.songListId)
            is AddSelectSongIntent.ToggleSelection -> toggleSelection(intent.songId)
            is AddSelectSongIntent.AddSelectToSongList -> addSelectToSongList(intent.songListId)
            is AddSelectSongIntent.ChangeSearchWord -> changeSearchWord(
                intent.songListId,
                intent.word
            )
        }
    }

    private fun loadData(songListId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            // 进入歌单页面 加载数据
            val songListWithSongs = songListDao.getSongsBySongListId(songListId)
            val songs = songListWithSongs?.songs ?: emptyList()

            val selectedSongIds = songs.map { it.uniqueId }.toSet()

            songDao.queryAll().collect { items ->
                // 过滤已有歌曲
                val unselectedSongs = items.filter { it.uniqueId !in selectedSongIds }
                _unselectSongs.value = unselectedSongs
                _isLoading.value = false
            }
        }
    }

    private fun toggleSelection(id: String) {
        val current = _selectedIds.value.toMutableSet()
        if (id in current) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _selectedIds.value = current
    }

    private fun addSelectToSongList(songListId: Long) {
        val list =
            _selectedIds.value.map { SongListWithSongEntity(songlistId = songListId, songId = it) }

        viewModelScope.launch {
            songListDao.insertSongsToSongList(list)
            val songList = songListDao.getSongList(songListId)
            val newNongList = songList.copy(count = songList.count + list.size)
            songListDao.updateSongList(newNongList)
        }

        _searchWord.value = null
        _selectedIds.value = emptySet<String>()
        //
        _effect.trySend(AddSelectSongEffect.ShowMessage("success"))
        _effect.trySend(AddSelectSongEffect.Back)
    }

    private fun changeSearchWord(songListId: Long, word: String?) {
        _searchWord.value = word
        _isLoading.value = true

        viewModelScope.launch {
            val songListWithSongs = songListDao.getSongsBySongListId(songListId)
            val songs = songListWithSongs?.songs ?: emptyList()

            val selectedSongIds = songs.map { it.uniqueId }.toSet()

            songDao.queryLike(word).collect { items ->
                // 过滤已有歌曲
                val unselectedSongs = items.filter { it.uniqueId !in selectedSongIds }
                _unselectSongs.value = unselectedSongs
                _isLoading.value = false
            }
        }

    }
}