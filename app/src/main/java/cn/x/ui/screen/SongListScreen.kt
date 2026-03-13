package cn.x.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.ui.componets.CenterTopBar
import cn.x.ui.componets.SongItemWidget
import cn.x.util.toMediaItem

@Composable
fun SongListScreen(
    songListScreenVM: SongListScreenVM = hiltViewModel(),
    onDrawerToggle: () -> Unit
) {
    val songLists by songListScreenVM.songLists.collectAsState()
    Scaffold(
        topBar = {
            CenterTopBar(
                title = "SongList",
                onDrawerToggle,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(songLists) { index, songListItem ->
                Text(songListItem.name)
            }
        }
    }
}