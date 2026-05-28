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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


class LocalSongScreenVM(
    private val songDao: SongDao,
    private val songListDao: SongListDao,
    val playerController: PlayerController,
) : ViewModel() {

    // 歌曲
    val songs: StateFlow<List<SongEntity>> = songDao.queryAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3000),
        initialValue = emptyList()
    )
    // 歌单
    val songLists:StateFlow<List<SongListEntity>> = songListDao.getAllSongLists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3000),
        initialValue = emptyList()
    )


    fun play(song: MediaItem) {
        playerController.replaceAll(songs.value.map { it.toMediaItem() }, song)
    }


}