package cn.x.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.db.MusicDatabase
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListWithSongEntity
import cn.x.ui.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddSelectSongScreenVM @Inject constructor(
    private val db: MusicDatabase,
) : ViewModel() {

    // 非歌单歌曲
    private val _unselectSongs = MutableStateFlow<List<SongEntity>>(emptyList())
    val unselectSongs: StateFlow<List<SongEntity>> = _unselectSongs

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds

    fun loadData(songListId: Long) {
        // 进入歌单页面 加载数据
        viewModelScope.launch {
            val songs = withContext(Dispatchers.IO) {
                db.SongListDao().getSongsBySongListId(songListId)?.songs ?: emptyList()
            }
            // 添加歌曲页：加载未选中的歌曲
            val selectedSongIds = songs.map { it.uniqueId }.toSet() // 在 Main 线程获取快照

            val unselectedSongs = withContext(Dispatchers.IO) {
                val allSongs = db.SongDao().queryAll()
                allSongs.filter { it.uniqueId !in selectedSongIds }
            }

            _unselectSongs.value = unselectedSongs // 在 Main 线程更新
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
                db.SongListDao().insertSongsToSongList(list)
            }

        }

    }
}