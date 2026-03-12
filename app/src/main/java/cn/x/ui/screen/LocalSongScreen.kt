package cn.x.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.ui.componets.SongItemWidget
import cn.x.util.toMediaItem

@Composable
fun LocalSongScreen(
    localSongScreenVM: LocalSongScreenVM = hiltViewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val songList by localSongScreenVM.songList.collectAsState()

    Box(
        modifier = Modifier.background(colorScheme.primaryContainer)
    ) {

        LazyColumn() {
            itemsIndexed(songList) { index, songItem ->
                SongItemWidget(title = songItem.title,
                    artist = songItem.artist,
                    duration = songItem.duration,
                    onClick = {localSongScreenVM.play(songItem.toMediaItem())},
                    onMenuClick = {}
                )
            }
        }
    }
}