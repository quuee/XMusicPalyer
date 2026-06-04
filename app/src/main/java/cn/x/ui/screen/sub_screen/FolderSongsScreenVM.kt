package cn.x.ui.screen.sub_screen

import androidx.lifecycle.ViewModel
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class FolderSongsScreenVM (
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

//    private val parentPath: String? = savedStateHandle[Constants.FolderPath]
//    private val _searchWord = MutableStateFlow<String?>(null)

}