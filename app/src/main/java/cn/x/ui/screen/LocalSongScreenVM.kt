package cn.x.ui.screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import cn.x.data.MusicDatabase
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListEntity
import cn.x.service.PlayerController
import cn.x.util.toMediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalSongScreenVM @Inject constructor(
    private val db: MusicDatabase,
    val playerController: PlayerController,
) : ViewModel() {


    private val tag = "LocalSongScreenVM"
    // 歌曲
    private val _songs = MutableStateFlow<List<SongEntity>>(emptyList())
    val songs: StateFlow<List<SongEntity>> = _songs.asStateFlow()

    // 歌单
    private val _songLists = MutableStateFlow<List<SongListEntity>>(emptyList())
    val songLists: StateFlow<List<SongListEntity>> = _songLists.asStateFlow()

    // 选中的歌曲
    private val _selectSong = MutableStateFlow<SongEntity?>(null)
//    val selectSong = _selectSong.asStateFlow()

    // 底部开关
    private val _bottomSheetVisible = MutableStateFlow(false)
    val bottomSheetVisible = _bottomSheetVisible.asStateFlow()

    // 选者歌单弹窗开关
    private val _songListDialogVisible = MutableStateFlow(false)
    val songListDialogVisible = _songListDialogVisible.asStateFlow()

    // 搜索关键字
    private val _searchWord = MutableStateFlow<String?>(null)


    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) { // 使用 IO 调度器以确保在后台线程运行
            try {
                _songs.value = db.SongDao().queryAll() // 这行现在在后台线程执行
                _songLists.value = db.SongListDao().getAllSongLists()
            } catch (e: Exception) {
                // 可以在这里设置一个错误状态或空列表
            }
        }
    }

    fun search(searchWord: String?) {
        // 处理空字符串为 null，触发“忽略条件”逻辑
        val normalizedQuery = if (searchWord.isNullOrBlank()) null else "%${searchWord}%"
        _searchWord.value = normalizedQuery

    }

    fun play(song: MediaItem) {
        playerController.replaceAll(_songs.value.map { it.toMediaItem() }, song)
    }

    fun showBottomSheet(song: SongEntity) {
        _bottomSheetVisible.value = true
        _selectSong.value = song
    }

    fun hideBottomSheet() {
        _bottomSheetVisible.value = false
        _selectSong.value = null
    }

    fun showSongListDialog() {
        _songListDialogVisible.value = true
        // 或者 打开时才加载歌单
    }

    fun hideSongListDialog() {
        _songListDialogVisible.value = false
    }

}