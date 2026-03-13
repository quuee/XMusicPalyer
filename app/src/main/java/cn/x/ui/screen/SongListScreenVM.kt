package cn.x.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import cn.x.data.db.MusicDatabase
import cn.x.data.db.SongListEntity
import cn.x.service.PlayerController
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
        // 在 viewModelScope 内启动一个协程
        viewModelScope.launch(Dispatchers.IO) { // 使用 IO 调度器以确保在后台线程运行
            try {
                val songLists = db.SongListDao().getAllSongLists() // 这行现在在后台线程执行
                _songLists.value = songLists

            } catch (e: Exception) {
                // 可以在这里设置一个错误状态或空列表
            }
        }
    }
}