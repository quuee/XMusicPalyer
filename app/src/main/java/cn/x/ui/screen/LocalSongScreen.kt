package cn.x.ui.screen


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.ui.componets.CenterTopBar
import cn.x.ui.componets.MultiSelectSongItem
import cn.x.ui.componets.SongItemWidget
import cn.x.util.toMediaItem

@Composable
fun LocalSongScreen(
    localSongScreenVM: LocalSongScreenVM = hiltViewModel(),
    onDrawerToggle: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val songList by localSongScreenVM.songList.collectAsState()

    Scaffold(
        topBar = {
            CenterTopBar(
                "LocalSong",
                onDrawerToggle,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(songList) { index, songItem ->

                MultiSelectSongItem(
                    song = songItem,
                    isSelected = false,
                    isSelectionMode = false,
                    onClick = { localSongScreenVM.play(songItem.toMediaItem()) },
                    onMenuClick = {},
                    onToggleSelection = { }
                )
            }
        }

    }

}