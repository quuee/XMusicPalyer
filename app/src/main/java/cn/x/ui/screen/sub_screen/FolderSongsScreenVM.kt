package cn.x.ui.screen.sub_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.x.data.MusicDatabase
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListEntity
import cn.x.data.paging.SongPagingSource
import cn.x.service.PlayerController
import cn.x.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class FolderSongsScreenVM @Inject constructor(
    private val db: MusicDatabase,
    private val playerController: PlayerController,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val tag = "LocalSongScreenVM"

    private val _songLists = MutableStateFlow<List<SongListEntity>>(emptyList())
    val songLists: StateFlow<List<SongListEntity>> = _songLists.asStateFlow()

    private val _selectSong = MutableStateFlow<SongEntity?>(null)
//    val selectSong = _selectSong.asStateFlow()

    private val _bottomSheetVisible = MutableStateFlow(false)
    val bottomSheetVisible = _bottomSheetVisible.asStateFlow()

    private val _songListDialogVisible = MutableStateFlow(false)
    val songListDialogVisible = _songListDialogVisible.asStateFlow()

    private val parentPath: String? = savedStateHandle[Constants.FolderPath]
    private val _searchWord = MutableStateFlow<String?>(null)

    // 暴露给 UI 的数据流：PagingData<Song>
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val songsFlow: Flow<PagingData<SongEntity>> = _searchWord
        .debounce(300) // 防抖：用户停止输入 300ms 后再搜索
        .flatMapLatest { searchWord ->
            // 每次查询变化，重新构建 Pager
            Pager(
                config = PagingConfig(
                    pageSize = 10, // 每页 20 条
                    enablePlaceholders = false,
                    initialLoadSize = 20
                ),
                pagingSourceFactory = {
                    SongPagingSource(
                        db.SongDao(),
                        searchWord = searchWord,
                        parentPath = parentPath
                    )
                }
            ).flow
        }
        .cachedIn(viewModelScope) // 在 ViewModel 作用域内缓存分页数据

    init {

        Log.d("DEBUG", "$parentPath ")
    }

    fun search(searchWord: String?) {
        // 处理空字符串为 null，触发“忽略条件”逻辑
        val normalizedQuery = if (searchWord.isNullOrBlank()) null else "%${searchWord}%" // SQLite 默认的 LIKE 运算符对多字节字符（如中文）的支持有限
        _searchWord.value = normalizedQuery

    }

    fun play(song: MediaItem) {
//        playerController.replaceAll(_songs.value.map { it.toMediaItem() }, song)
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
        //  打开时才加载歌单
    }

    fun hideSongListDialog() {
        _songListDialogVisible.value = false
    }

}