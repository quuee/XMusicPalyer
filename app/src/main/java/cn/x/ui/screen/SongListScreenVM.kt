package cn.x.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.MusicDatabase
import cn.x.data.db.SongListEntity
import cn.x.util.getCurrentDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SongListScreenVM @Inject constructor(
    private val db: MusicDatabase,
) : ViewModel() {

    private val _songLists = MutableStateFlow<List<SongListEntity>>(emptyList())
    val songLists = _songLists.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()


    private val EmptySongList = SongListEntity(
        id = 0L,
        name = "",
        cover = "",
        count = 0,
        createDate = "",
        sort = 1
    )
    private val _createOrRenameSongList = MutableStateFlow(
        EmptySongList
    )
    val createOrRenameSongList = _createOrRenameSongList.asStateFlow()

    fun loadSongLists() {
        // 从 repository 加载歌单列表
        viewModelScope.launch(Dispatchers.IO) {
            _songLists.value = db.SongListDao().getAllSongLists()
        }
    }

    fun openDialog(songListId: Long?) {
        _showDialog.value = true
        if (songListId != null && songListId != 0L) {
            val item = _songLists.value.first { songList -> songList.id == songListId }
            _createOrRenameSongList.value = item
        }

    }

    fun dismissDialog() {
        _showDialog.value = false
        _createOrRenameSongList.value = EmptySongList.copy()
    }


    // 更新输入框内容
    fun onNewSongListNameChange(name: String) {
        _createOrRenameSongList.value = EmptySongList.copy(
            id = _createOrRenameSongList.value.id,
            name = name,
            createDate = getCurrentDateTime()
        )
    }

    // 确认创建
    fun songListConfirm(songListId: Long?) {
        val name = _createOrRenameSongList.value.name.trim()
        if (name.isEmpty()) return // 简单校验
        viewModelScope.launch {
            if (songListId == null || songListId == 0L) {
                // create
                withContext(Dispatchers.IO) {
                    db.SongListDao().insertSongList(_createOrRenameSongList.value)
                    loadSongLists() // 刷新列表
                }
            } else {
                // update
                val oldItem = _songLists.value.first { songList -> songList.id == songListId }
                val newItem = oldItem.copy(name = _createOrRenameSongList.value.name)
                withContext(Dispatchers.IO) {
                    db.SongListDao().updateSongList(newItem)
                    loadSongLists() // 刷新列表
                }
            }
            dismissDialog() // 这里是异步,防止dismissDialog把状态重置,只能放里面
        }
    }

    fun delete(songListId: Long) {
        val item = _songLists.value.first { songList -> songList.id == songListId }
        viewModelScope.launch {
            // 调用实际的业务逻辑
            withContext(Dispatchers.IO) {
                db.SongListDao().delete(item)
                db.SongListDao().deleteAllBySongListId(songListId)
                loadSongLists() // 刷新列表
            }
            dismissDialog()
        }
    }
}