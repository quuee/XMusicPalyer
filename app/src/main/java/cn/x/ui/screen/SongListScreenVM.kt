package cn.x.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.MusicDatabase
import cn.x.data.db.SongListEntity
import cn.x.service.PlayerController
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

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog = _showCreateDialog.asStateFlow()

    private val _newSongListName = MutableStateFlow("")
    val newSongListName = _newSongListName.asStateFlow()

    fun loadSongLists() {
        // 从 repository 加载歌单列表
        viewModelScope.launch(Dispatchers.IO) {
            _songLists.value = db.SongListDao().getAllSongLists()
        }
    }

    fun openCreateDialog(){
        _showCreateDialog.value = true
    }

    fun dismissCreateDialog() {
        _showCreateDialog.value = false
        _newSongListName.value = ""
    }

    // 更新输入框内容
    fun onNewSongListNameChange(name: String) {
        _newSongListName.value = name
    }

    // 确认创建
    fun createSongListConfirm() {
        val name = _newSongListName.value.trim()
        if (name.isEmpty()) return // 简单校验

        val s = SongListEntity(0, name, "", 0, getCurrentDateTime(),1)
        viewModelScope.launch {
            // 调用实际的业务逻辑
            withContext(Dispatchers.IO) {
                db.SongListDao().insertSongList(s)
                loadSongLists() // 刷新列表
            }
            // 成功后关闭弹窗并重置
            dismissCreateDialog()
        }
    }
}