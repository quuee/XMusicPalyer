package cn.x.ui.screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.ui.componets.CenterTopBar
import cn.x.ui.componets.MultiSelectSongItem
import cn.x.util.toMediaItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSongScreen(
    localSongScreenVM: LocalSongScreenVM = hiltViewModel(),
    onDrawerToggle: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val songList by localSongScreenVM.songList.collectAsState()
    // 👇 控制 BottomSheet 是否显示
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 只有在 showBottomSheet 为 true 时才显示
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    showBottomSheet = false
                }
            },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    Icon(imageVector = Icons.Default.Add,contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text("添加到歌单")
                }
                Row(modifier = Modifier.padding(4.dp)) {
                    Icon(imageVector = Icons.Default.Share,contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text("分享")
                }
                Row(modifier = Modifier.padding(4.dp)) {
                    Icon(imageVector = Icons.Default.Info,contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text("歌曲信息")
                }
                Row(modifier = Modifier.padding(4.dp)) {
                    Icon(imageVector = Icons.Default.Delete,contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text("永久删除")
                }
            }
        }
    }

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
                    onMenuClick = {
                        showBottomSheet = true
                    },
                    onToggleSelection = { }
                )
            }
        }

    }
}