package cn.x.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.db.MusicDatabase
import cn.x.data.db.SongListEntity
import cn.x.service.PlayerController
import cn.x.util.getCurrentDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongListScreenVM @Inject constructor(
    private val db: MusicDatabase,
    private val playerController: PlayerController
) : ViewModel() {

    private val _songLists = MutableStateFlow<List<SongListEntity>>(emptyList())
    val songLists = _songLists.asStateFlow()

    init {
        loadSongLists()
    }

    private fun loadSongLists() {
        // 从 repository 加载歌单列表
        viewModelScope.launch(Dispatchers.IO) {
            _songLists.value = db.SongListDao().getAllSongLists()
        }
    }

    fun createSongList(name: String) {
        if (name.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                val s = SongListEntity(0, name, "", 0, getCurrentDateTime())
                db.SongListDao().insertSongList(s)
                loadSongLists() // 刷新列表
            }
        }
    }
}