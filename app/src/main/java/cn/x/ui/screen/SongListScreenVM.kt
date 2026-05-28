package cn.x.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.dao.SongListDao
import cn.x.data.db.SongListEntity
import cn.x.util.getCurrentDateTime
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


sealed class SongListIntent {
    data class OnSongListNameChange(val name: String) : SongListIntent()
    data class OpenRename(val songListEntity: SongListEntity) : SongListIntent()
    data object SubmitForm : SongListIntent()
    data class Delete(val songListId: Long) : SongListIntent()
}

sealed class SongListEffect {
    data class ShowMessage(val message: String) : SongListEffect()
    data object Back : SongListEffect()
}

class SongListScreenVM(
    private val songListDao: SongListDao,
) : ViewModel() {

    // 私有副作用通道 (Channel 用于处理一次性事件)
    private val _effect = Channel<SongListEffect>(Channel.BUFFERED)
    val effect: Flow<SongListEffect> = _effect.receiveAsFlow()

    private val _songLists: StateFlow<List<SongListEntity>> = songListDao.getAllSongLists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1000),
        initialValue = emptyList()
    )
    val songLists = _songLists


    private val _songListFormData = MutableStateFlow(
        SongListFormState()
    )
    val songListFormData = _songListFormData.asStateFlow()

    fun handleIntent(intent: SongListIntent) {
        when (intent) {
            is SongListIntent.OnSongListNameChange -> onSongListNameChange(intent.name)
            is SongListIntent.OpenRename -> openRename(intent.songListEntity)
            is SongListIntent.SubmitForm -> submitForm()
            is SongListIntent.Delete -> delete(intent.songListId)
        }
    }


    private fun openRename(songListEntity: SongListEntity) {
        _songListFormData.value = SongListFormState(
            id = songListEntity.id,
            name = songListEntity.name,
            cover = songListEntity.cover,
        )
    }


    // 更新输入框内容
    private fun onSongListNameChange(name: String) {
        _songListFormData.value = _songListFormData.value.copy(
            name = name,
        )
    }

    // 确认创建
    private fun submitForm() {
        val name = _songListFormData.value.name.trim()
        if (name.isEmpty()) {
            _effect.trySend(SongListEffect.ShowMessage("not empty"))
            return // 简单校验
        }

        viewModelScope.launch {
            if (_songListFormData.value.id == null || _songListFormData.value.id == 0L) {
                // create
                val s =  SongListEntity(
                    id = 0L,
                    name = _songListFormData.value.name,
                    cover = "",
                    count = 0,
                    createDate = getCurrentDateTime(),
                    sort = _songLists.value.size
                )
                songListDao.insertSongList(s)
            } else {
                // update
                val oldItem = _songLists.value.first { songList -> songList.id == _songListFormData.value.id }
                val newItem = oldItem.copy(name = _songListFormData.value.name)
                songListDao.updateSongList(newItem)
            }
            // reset
            _songListFormData.value = SongListFormState()
        }
    }

    private fun delete(songListId: Long) {
        val item = _songLists.value.first { songList -> songList.id == songListId }
        viewModelScope.launch {
            songListDao.delete(item)
            songListDao.deleteAllBySongListId(songListId)
        }
    }
}

data class SongListFormState(
    val id: Long? = null,
    val name: String = "",
    val cover: String = "",
)