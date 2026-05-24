package cn.x.ui.screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.db.FolderEntity
import cn.x.data.dao.FolderDao
import cn.x.service.PlayerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class FolderScreenVM (
    private val folderDao: FolderDao,
    private val playerController: PlayerController
) : ViewModel() {

    val loading = MutableStateFlow(false)

    val errorMsg = MutableStateFlow<String?>(null)

    private val _currentFolders = MutableStateFlow<List<FolderEntity>>(emptyList())
    val currentFolders = _currentFolders.asStateFlow()

    init {
        viewModelScope.launch {
            folderDao.getAllFolders().collect { itmes ->
                _currentFolders.value  = itmes
            }
        }
    }
}