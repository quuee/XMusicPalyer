package cn.x.ui.screen.sub_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import cn.x.data.MusicDatabase
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListEntity
import cn.x.service.PlayerController
import cn.x.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest


class FolderSongsScreenVM (
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

}