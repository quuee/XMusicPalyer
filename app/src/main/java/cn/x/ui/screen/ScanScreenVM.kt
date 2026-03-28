package cn.x.ui.screen


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.db.FolderEntity
import cn.x.data.MusicDatabase
import cn.x.data.db.SongEntity
import cn.x.util.MusicScanUtilByMediaStoreFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScanScreenVM @Inject constructor(
    private val musicScanFlow: MusicScanUtilByMediaStoreFlow,
    private val db: MusicDatabase,
) : ViewModel() {

    private val TAG = "ScanScreenVM"

    // 使用MutableStateFlow存储扫描到的音乐列表
    private val _musicList = MutableStateFlow<List<SongEntity>>(emptyList())
    val musicList: StateFlow<List<SongEntity>> = _musicList.asStateFlow()

    // 扫描状态
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    // 已有文件夹目录集合
    private val _currentFolders = MutableStateFlow<List<FolderEntity>>(emptyList())
    val currentFolders = _currentFolders.asStateFlow()

    // 开始扫描音乐
    fun startScanByMediaStore(folderPath: String? = null, minDuration: Long = 60_000) {
        viewModelScope.launch {
            _scanState.value = ScanState.Scanning
            _musicList.value = emptyList() // 清空之前的列表
//            var count = 0
            try {
                val flow = musicScanFlow.scanAllMusicAsFlow(minDuration)

                flow.collect { songItem ->
                    Log.d(TAG, "startScan: $songItem")
                    // 每收到一首歌就更新歌曲列表
                    _musicList.value += songItem
//                    count++
                    delay(10)
                }

                _currentFolders.value = _musicList.value
                    .groupingBy { it.relativePath }
                    .eachCount().map { FolderEntity(it.key, it.value) }

//                Log.d(TAG, "startScanByMediaStore: count:$count")
                // *** 关键修改：将数据库操作移到后台线程 ***
                withContext(Dispatchers.IO) {
                    db.SongDao().insertAll(_musicList.value)
                    db.FolderDao().insertAll(_currentFolders.value)
                }

                _scanState.value = ScanState.Completed
            } catch (e: Exception) {
                Log.d(TAG, "startScan: ${e.message}")
                _scanState.value = ScanState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onDismiss() {
        _scanState.value = ScanState.Idle
    }



}

sealed class ScanState {
    object Idle : ScanState()
    object Scanning : ScanState()
    object Completed : ScanState()
    data class Error(val message: String) : ScanState()
}

