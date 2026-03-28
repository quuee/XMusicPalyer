package cn.x.ui.screen.sub_screen

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cn.x.R
import cn.x.data.db.SongListEntity
import cn.x.ui.componets.AlphabetIndexSidebar
import cn.x.ui.componets.MultiSelectSongItem
import cn.x.util.Constants
import cn.x.util.toMediaItem
import kotlinx.coroutines.launch

/**
 * 展示不同文件夹下的歌曲
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderSongsScreen(
    naviBack: () -> Unit,
    folderSongsScreenVM: FolderSongsScreenVM=hiltViewModel(),
    path: String
) {
    val songs = folderSongsScreenVM.songsFlow.collectAsLazyPagingItems()
    val songLists by folderSongsScreenVM.songLists.collectAsState()

    val songListDialogVisible by folderSongsScreenVM.songListDialogVisible.collectAsState()
    // 控制 BottomSheet 是否显示
    val bottomSheetVisible by folderSongsScreenVM.bottomSheetVisible.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val letters = (listOf("#") + Constants.alphabet)
    val listState = rememberLazyListState()


    // 只有在 showBottomSheet 为 true 时才显示
    if (bottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                folderSongsScreenVM.hideBottomSheet()
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
                        .clickable(onClick = { folderSongsScreenVM.showSongListDialog() })
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(stringResource(R.string.song_addTo_songList))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(stringResource(R.string.song_share))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(stringResource(R.string.song_info))
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
                    Text(stringResource(R.string.song_delete))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {Text(path)},
                navigationIcon = {
                    IconButton(onClick = {
                        naviBack()
                    }) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "back")
                    }
                },
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)){

            LazyColumn() {
                items(count = songs.itemCount) { index ->
                    val song = songs[index]
                    song?.let{
                        MultiSelectSongItem(
                            song = song,
                            isSelected = false,
                            isSelectionMode = false,
                            isCurrent = false,
                            onClick = { folderSongsScreenVM.play(song.toMediaItem()) },
                            onMenuClick = {
                                folderSongsScreenVM.showBottomSheet(song)
                            },
                            onToggleSelection = { }
                        )
                    }

                }

                // 处理加载状态
                when {
                    songs.loadState.refresh is LoadState.Loading -> {
                        item { Text("refresh") }
                    }
                    songs.loadState.append is LoadState.Loading -> {
                        item { Text("append") }
                    }
                    songs.loadState.refresh is LoadState.Error -> {
                        item { Text("Error") }
                    }
                }
            }


            // 侧边字母索引
            AlphabetIndexSidebar(
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
                onDismiss = { folderSongsScreenVM.hideSongListDialog() }
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
