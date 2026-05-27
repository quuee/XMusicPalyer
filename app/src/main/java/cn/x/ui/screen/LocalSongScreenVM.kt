package cn.x.ui.screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import cn.x.data.dao.SongDao
import cn.x.data.dao.SongListDao
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListEntity
import cn.x.service.PlayerController
import cn.x.util.toMediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LocalSongScreenVM(
    private val songDao: SongDao,
    private val songListDao: SongListDao,
    val playerController: PlayerController,
) : ViewModel() {

    // 歌曲
    val songs: StateFlow<List<SongEntity>> = songDao.queryAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 歌单
    private val _songLists = MutableStateFlow<List<SongListEntity>>(emptyList())
    val songLists: StateFlow<List<SongListEntity>> = _songLists.asStateFlow()

    // 选中的歌曲
    private val _selectedSong = MutableStateFlow<SongEntity?>(null)
    val selectedSong = _selectedSong.asStateFlow()

    // 底部开关
    private val _bottomSheetVisible = MutableStateFlow(false)
    val bottomSheetVisible = _bottomSheetVisible.asStateFlow()

    // 选择歌单弹窗开关
    private val _songListDialogVisible = MutableStateFlow(false)
    val songListDialogVisible = _songListDialogVisible.asStateFlow()

    // 歌曲信息弹窗开关
    private val _songInfoDialogVisible = MutableStateFlow(false)
    val songInfoDialogVisible = _songInfoDialogVisible.asStateFlow()

    // 搜索关键字
    private val _searchWord = MutableStateFlow<String?>(null)


    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
//            songDao.queryAll().collect { items ->
//                songs.value = items
//            }
            songListDao.getAllSongLists().collect { items ->
                _songLists.value = items
            }
        }
    }

    fun search(searchWord: String?) {
        // 处理空字符串为 null，触发“忽略条件”逻辑
        val normalizedQuery = if (searchWord.isNullOrBlank()) null else "%${searchWord}%"
        _searchWord.value = normalizedQuery

    }

    fun play(song: MediaItem) {
        playerController.replaceAll(songs.value.map { it.toMediaItem() }, song)
    }

    fun showBottomSheet(song: SongEntity) {
        _bottomSheetVisible.value = true
        _selectedSong.value = song
    }

    fun hideBottomSheet() {
        _bottomSheetVisible.value = false
        _selectedSong.value = null
    }

    fun showSongListDialog() {
        _songListDialogVisible.value = true
        // 或者 打开时才加载歌单
    }

    fun hideSongListDialog() {
        _songListDialogVisible.value = false
    }

    fun showSongInfoDialog() {
        _songInfoDialogVisible.value = true
        // 或者 打开时才 用jaudiotagger读取更详细信息
    }

    fun hideSongInfoDialog() {
        _songInfoDialogVisible.value = false
    }

}