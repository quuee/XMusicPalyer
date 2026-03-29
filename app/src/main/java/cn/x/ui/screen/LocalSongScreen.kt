package cn.x.ui.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.R
import cn.x.data.db.SongListEntity
import cn.x.ui.Screens
import cn.x.ui.componets.AlphabetIndexSidebar
import cn.x.ui.componets.CenterTopBar
import cn.x.ui.componets.MultiSelectSongItem
import cn.x.util.Constants
import cn.x.util.getSongId
import cn.x.util.toMediaItem
import kotlinx.coroutines.launch

/**
 * 本地所有歌曲
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSongScreen(
    localSongScreenVM: LocalSongScreenVM = hiltViewModel(),
    onDrawerToggle: () -> Unit,
    naviRouteItem: (String) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val songs by localSongScreenVM.songs.collectAsState()
    val songLists by localSongScreenVM.songLists.collectAsState()

    // 添加到歌单 dialog
    val songListDialogVisible by localSongScreenVM.songListDialogVisible.collectAsState()

    // 控制 BottomSheet 是否显示
    val bottomSheetVisible by localSongScreenVM.bottomSheetVisible.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val letters = (listOf("#") + Constants.alphabet)
    val alphabetIndexListState = rememberLazyListState()

    val controller = localSongScreenVM.playerController
    val currentSong by controller.currentSong.collectAsState()


    Scaffold(
        topBar = {
            CenterTopBar(
                stringResource(R.string.local_song),
                onDrawerToggle,
                actions = {
                    IconButton(onClick = { naviRouteItem(Screens.Search.route) }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                })
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            LazyColumn() {
                itemsIndexed(songs) { index, songItem ->
                    MultiSelectSongItem(
                        song = songItem,
                        isSelected = false,
                        isSelectionMode = false,
                        isCurrent = currentSong?.getSongId() == songItem.songId,
                        onClick = { localSongScreenVM.play(songItem.toMediaItem()) },
                        onMenuClick = {
                            localSongScreenVM.showBottomSheet(songItem)
                        },
                        onToggleSelection = { }
                    )
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
                            alphabetIndexListState.animateScrollToItem(firstIndex)
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

        if (bottomSheetVisible) {
            SongBottomSheet(
                onDismissRequest = {
                    localSongScreenVM.hideBottomSheet()
                    scope.launch {
                        sheetState.hide()
                    }
                },
                onAddToPlaylist = { localSongScreenVM.showSongListDialog() },
                onShare = { /* 处理分享逻辑 */ },
                onPlayNext = { /* 处理下一首逻辑 */ },
                onShowInfo = { /* 处理信息逻辑 */ },
                onEditMetadata = { /* 处理编辑逻辑 */ },
                onDelete = { /* 处理删除逻辑 */ }
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
                .heightIn(min = (LocalConfiguration.current.screenHeightDp / 2).dp) // 根据屏幕物理高度获取
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    "添加到歌单",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold
                )
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SongBottomSheet(
    onDismissRequest: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onShare: () -> Unit,
    onPlayNext: () -> Unit,
    onShowInfo: () -> Unit,
    onEditMetadata: () -> Unit,
    onDelete: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 每一个 Row 都是一个菜单项
            BottomSheetItem(
                icon = Icons.Default.Add,
                text = "添加到歌单",
                onClick = onAddToPlaylist
            )
            BottomSheetItem(
                icon = Icons.Default.Share,
                text = "分享",
                onClick = onShare
            )
            BottomSheetItem(
                icon = Icons.Default.PlaylistAdd,
                text = "下一首播放",
                onClick = onPlayNext
            )
            BottomSheetItem(
                icon = Icons.Default.Info,
                text = "歌曲信息",
                onClick = onShowInfo
            )
            BottomSheetItem(
                icon = Icons.Default.Edit,
                text = "编辑元信息",
                onClick = onEditMetadata
            )
            BottomSheetItem(
                icon = Icons.Default.Delete,
                text = "永久删除",
                onClick = onDelete,
                iconTint = Color.Red,
                textColor = Color.Red
            )
        }
    }
}


@Composable
private fun BottomSheetItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint
        )
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            text = text,
            color = textColor
        )
    }
}