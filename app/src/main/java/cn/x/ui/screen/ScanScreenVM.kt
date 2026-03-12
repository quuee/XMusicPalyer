package cn.x.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.db.MusicDatabase
import cn.x.data.db.SongEntity
import cn.x.util.MusicScanFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScanScreenVM @Inject constructor(
    private val musicScanFlow: MusicScanFlow,
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
    private val _currentFolders = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val currentFolders = _currentFolders.asStateFlow()

    // 开始扫描音乐
    fun startScan(folderPath: String? = null, minDuration: Long = 60_000) {
        viewModelScope.launch {
            _scanState.value = ScanState.Scanning
            _musicList.value = emptyList() // 清空之前的列表

            try {
                val flow = if (folderPath != null) {
                    musicScanFlow.scanMusicInFolderAsFlow(folderPath, minDuration)
                } else {
                    musicScanFlow.scanAllMusicAsFlow(minDuration)
                }

                flow.collect { songItem ->
                    Log.d(TAG, "startScan: $songItem")

                    // 1. 获取父文件夹路径
                    val songParentPath = songItem.path.substringBeforeLast("/")

                    // 2. 使用 update 原子性更新 StateFlow
                    _currentFolders.update { currentMap ->
                        currentMap.toMutableMap().apply {
                            // 3. 合并或添加新的 Uri
                            put(
                                songParentPath,
                                (currentMap[songParentPath] ?: emptySet()) + songItem.contentUri
                            )
                        }
                    }

                    // 每收到一首歌就更新歌曲列表
                    _musicList.value += songItem
                    delay(200)
                }

                // *** 关键修改：将数据库操作移到后台线程 ***
                withContext(Dispatchers.IO) {
                    db.SongDao().insertAll(_musicList.value)
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

