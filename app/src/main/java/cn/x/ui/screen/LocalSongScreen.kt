package cn.x.ui.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.data.db.SongListEntity
import cn.x.ui.componets.AlphabetIndexer
import cn.x.ui.componets.CenterTopBar
import cn.x.ui.componets.MultiSelectSongItem
import cn.x.util.Constants
import cn.x.util.toMediaItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSongScreen(
    localSongScreenVM: LocalSongScreenVM = hiltViewModel(),
    onDrawerToggle: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val songs by localSongScreenVM.songs.collectAsState()
    val songLists by localSongScreenVM.songLists.collectAsState()

    val songListDialogVisible by localSongScreenVM.songListDialogVisible.collectAsState()
    // 控制 BottomSheet 是否显示
    val bottomSheetVisible by localSongScreenVM.bottomSheetVisible.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val letters = (listOf("#") + Constants.alphabet)
    val listState = rememberLazyListState()


    // 只有在 showBottomSheet 为 true 时才显示
    if (bottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                localSongScreenVM.hideBottomSheet()
                // 等待动画结束再移除 UI
                scope.launch {
                    sheetState.hide()
                }
            },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable(onClick = { localSongScreenVM.showSongListDialog() })
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text("添加到歌单")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text("分享")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text("歌曲信息")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.Red
                    )
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

        Box(modifier = Modifier.fillMaxSize().padding(padding)){

            LazyColumn() {
                itemsIndexed(songs) { index, songItem ->

                    MultiSelectSongItem(
                        song = songItem,
                        isSelected = false,
                        isSelectionMode = false,
                        onClick = { localSongScreenVM.play(songItem.toMediaItem()) },
                        onMenuClick = {
                            localSongScreenVM.showBottomSheet(songItem)
                        },
                        onToggleSelection = { }
                    )
                }
            }

            // 侧边字母索引
            AlphabetIndexer(
                onLetterSelected = { letter ->
                    // 滚动到对应字母的位置
                    scope.launch {
                        val firstIndex = letters.indexOfFirst { it == letter }
                        if (firstIndex != -1) {
                            // 这里需要根据实际数据结构计算正确的索引
                            // 简化示例，实际需要更复杂的逻辑
                            listState.animateScrollToItem(firstIndex)
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }





        if (songListDialogVisible) {
            SongListDialog(
                songLists = songLists,
                onChoose = {},
                onDismiss = { localSongScreenVM.hideSongListDialog() }
            )
        }

    }
}

@Composable
private fun SongListDialog(
    songLists: List<SongListEntity>,
    onChoose: () -> Unit,
    onDismiss: () -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {
        // 完全自定义的内容
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn() {
                items(songLists) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(item.name)
                    }
                }
            }
        }


    }
}