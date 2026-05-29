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
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cn.x.R
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListEntity
import cn.x.route.LocalNavigator
import cn.x.route.Routes
import cn.x.ui.componets.AlphabetIndexSidebar
import cn.x.ui.componets.MultiSelectSongItem
import cn.x.util.Constants
import cn.x.util.ToastUtil
import cn.x.util.formatTime
import cn.x.util.getSongId
import cn.x.util.toMediaItem
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * 本地所有歌曲
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSongScreen(
    localSongScreenVM: LocalSongScreenVM = koinViewModel(),
    onDrawerToggle: () -> Unit,
) {
    // 导航
    val navigator = LocalNavigator.current
    // 歌曲
    val songs by localSongScreenVM.songs.collectAsState()
    // 歌单
    val songLists by localSongScreenVM.songLists.collectAsState()

    // 添加到歌单 dialog
    var songListDialogVisible by remember { mutableStateOf(false) }
    // 歌曲信息dialog
    var songInfoDialogVisible by remember { mutableStateOf(false) }

    // 控制 BottomSheet 是否显示
    var bottomSheetVisible by remember { mutableStateOf(false) }
    // 选中的歌曲
    var selectedSong: SongEntity? by remember { mutableStateOf(null) }

    // bottomSheet
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 侧边索引
    val letters = (listOf("#") + Constants.alphabet)
    val alphabetIndexListState = rememberLazyListState()

    val playerController = localSongScreenVM.playerController
    val currentSong by playerController.currentSong.collectAsState()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.local_song),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDrawerToggle) {
                        Icon(Icons.Filled.Menu, contentDescription = "Drawer Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { navigator.navigate(Routes.Search) }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                }
            )
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
                            bottomSheetVisible = true
                            selectedSong = songItem
                        },
                        onToggleSelection = { }
                    )
                }
            }


            // 侧边字母索引
            AlphabetIndexSidebar(
                onLetterSelected = { letter ->
                    // 滚动到对应字母的位置
                    coroutineScope.launch {
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
                onChoose = {
                    localSongScreenVM.addSelectToSongList(
                        songListId = it,
                        songId = selectedSong!!.uniqueId
                    )
                    songListDialogVisible = false
                    bottomSheetVisible = false
                    selectedSong = null
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                    ToastUtil.showSuccess("添加完成")
                },
                onDismiss = { songListDialogVisible = false }
            )
        }
        if (songInfoDialogVisible) {
            SongInfoDialog(song = selectedSong, onDismiss = { songInfoDialogVisible = false })
        }

        if (bottomSheetVisible) {
            SongBottomSheet(
                onDismissRequest = {
                    bottomSheetVisible = false
                    selectedSong = null
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                },
                onAddToPlaylist = { songListDialogVisible = true },
                onShare = { /* 处理分享逻辑 */ },
                onPlayNext = { /* 处理下一首逻辑 */ },
                onShowInfo = { songInfoDialogVisible = true },
                onEditMetadata = { /* 处理编辑逻辑 */ },
                onDelete = { /* 处理删除逻辑 */ }
            )
        }

    }
}

@Composable
private fun SongInfoDialog(
    song: SongEntity?,
    onDismiss: () -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {
        // 完全自定义的内容
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = (LocalConfiguration.current.screenHeightDp / 1.5).dp) // 根据屏幕物理高度获取
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // title
                Text(
                    "歌曲信息",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold
                )

                //content
                Text(song?.title ?: "")
                Text(song?.artist ?: "")
                Text(song?.album ?: "")
                Text(formatTime(song?.duration ?: 0L))
                Text(song?.fileName ?: "")
                Text(song?.fileSize.toString())
                Text(song?.absolutePath ?: "")

            }
        }
    }
}

@Composable
private fun SongListDialog(
    songLists: List<SongListEntity>,
    onChoose: (Long) -> Unit,
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
                                .clickable(onClick = { onChoose(item.id) })
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
                icon = Icons.AutoMirrored.Filled.PlaylistAdd,
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