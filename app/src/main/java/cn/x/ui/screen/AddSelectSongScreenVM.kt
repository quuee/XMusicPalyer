package cn.x.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.db.MusicDatabase
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListWithSongEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSelectSongScreenVM @Inject constructor(
    private val db: MusicDatabase,

    ) : ViewModel() {
    private val _items = MutableStateFlow<List<SongEntity>>(emptyList())
    val items: StateFlow<List<SongEntity>> = _items

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds

    init {
        // 模拟加载数据
        viewModelScope.launch(Dispatchers.IO) {
            _items.value =  db.SongDao().queryAll()
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


    fun addSelectToSongList(){
        val list = _selectedIds.value.map { SongListWithSongEntity(songlistId = 1L, songId = it) }

//        db.SongListDao().addSongToSongList(list)
    }
}