package cn.x.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.db.FolderEntity
import cn.x.data.db.MusicDatabase
import cn.x.service.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderScreenVM @Inject constructor(
    private val db: MusicDatabase,
    private val playerController: PlayerController
) : ViewModel() {

    private val _currentFolders = MutableStateFlow<List<FolderEntity>>(emptyList())
    val currentFolders = _currentFolders.asStateFlow()

    init {
        // 在 viewModelScope 内启动一个协程
        viewModelScope.launch(Dispatchers.IO) { // 使用 IO 调度器以确保在后台线程运行
            try {
                val folders = db.FolderDao().getAllFolders() // 这行现在在后台线程执行
                _currentFolders.value = folders

            } catch (e: Exception) {

                // 可以在这里设置一个错误状态或空列表
            }
        }
    }
}