package cn.x.ui.screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.db.MusicDatabase
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListEntity
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

) : ViewModel() {

    private val EMPTY = SongListEntity(0L, "", "", 0, "", -1)
    private val _songList = MutableStateFlow<SongListEntity>(EMPTY)
    val songList = _songList.asStateFlow()

    private val _songs = MutableStateFlow<List<SongEntity>>(emptyList())
    val songs: StateFlow<List<SongEntity>> = _songs.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    fun loadData(songListId:Long) {
        // 模拟加载数据
        viewModelScope.launch {
            val songListWithSongs = withContext(Dispatchers.IO) {
                db.SongListDao().getSongsBySongListId(songListId)
            }
            _songs.value = songListWithSongs?.songs?:emptyList() // 在 Main 线程更新
            _songList.value = songListWithSongs?.songList?: EMPTY
        }
    }

    fun toggleSelectionMode() {
        val newMode = !_isSelectionMode.value
        _isSelectionMode.value = newMode
        if (!newMode) {
            clearSelection()
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

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun isSelected(id: String): Boolean = id in _selectedIds.value

}