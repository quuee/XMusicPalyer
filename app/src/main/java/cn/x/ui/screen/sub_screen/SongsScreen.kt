package cn.x.ui.screen.sub_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.data.db.SongListEntity
import cn.x.service.PlayState
import cn.x.ui.Screens
import cn.x.ui.componets.FloatingBottomPlayerBar
import cn.x.ui.componets.ImageWidget
import cn.x.ui.componets.MultiSelectSongItem
import cn.x.util.getFileName
import cn.x.util.getSongId
import cn.x.util.toMediaItem


/**
 * 用于展示歌单歌曲的页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    naviBack: () -> Unit,
    naviRouteItem: (String) -> Unit,
    songsScreenVM: SongsScreenVM = hiltViewModel(),
    songListId: Long
) {

    val items by songsScreenVM.songs.collectAsState()
    val songList by songsScreenVM.songList.collectAsState()
    val selectedIds by songsScreenVM.selectedIds.collectAsState()
    val isSelectionMode by songsScreenVM.isSelectionMode.collectAsState()

    val isAllSelection = items.size == selectedIds.size

    val colorScheme = MaterialTheme.colorScheme
    val listState = rememberLazyListState()

    // 计算顶部区域是否还在可见范围内
    val topSectionHeightDp = with(LocalConfiguration.current) { screenHeightDp.dp / 3 }
    val density = LocalDensity.current

    // 获取顶部区域是否被滚出：如果 firstVisibleItemIndex > 0，说明顶部已完全滚出
    // 如果 index == 0，但 offset > 0，说明正在滚出
    val alpha by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                // 第一项（顶部区域）还在，计算透明度
                val maxOffsetPx = with(density) { topSectionHeightDp.toPx() }
                val progress =
                    (listState.firstVisibleItemScrollOffset / maxOffsetPx).coerceAtMost(1f)
                1f - progress
            } else {
                // 顶部已完全滚出
                0f
            }
        }
    }

    val controller = songsScreenVM.playerController
    // 1. 直接收集各个 StateFlow
    val currentSong by controller.currentSong.collectAsState()
    val playState by controller.playState.collectAsState()
    val isPlaying = playState == PlayState.Playing


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = songList.name,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        songsScreenVM.clearSelection()
                        naviBack()
                    }) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "back")
                    }
                },
                actions = {
                    if (!isSelectionMode) {
                        IconButton(onClick = {
                            naviRouteItem(Screens.AddSelectSong.route.plus("/${songListId}"))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            FloatingBottomPlayerBar(
                mediaItem = currentSong,
                isPlaying = isPlaying,
                onSongClick = { naviRouteItem(Screens.Player.route) },
                onNextClick = { controller.next() },
                onPreviousClick = { controller.prev() },
                onPlayPauseClick = { controller.playPause() },
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.navigationBars.only(
                        WindowInsetsSides.Bottom
                    )
                )
            )
        },
        // todo 和我的歌曲按钮有冲突
//        floatingActionButton = {
//            IconButton(onClick = {}) {
//                Icon(imageVector = Icons.Default.MyLocation, contentDescription = null)
//            }
//        }

    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 可滚动内容
            LazyColumn(
                state = listState,

                ) {
                item {
                    CoverSection(
                        songList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(topSectionHeightDp)
                            .alpha(alpha)
                    )
                }

                stickyHeader {
                    Toolbar(
                        isSelectionMode = isSelectionMode,
                        clearSelection = { songsScreenVM.clearSelection() },
                        allSelection = { songsScreenVM.allSelection() },
                        toggleSelectionMode = { songsScreenVM.toggleSelectionMode() },
                        refresh = { songsScreenVM.loadData() },
                        isAllSelection = isAllSelection,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(colorScheme.surfaceVariant)
                    )
                }

                itemsIndexed(items) { index, song ->
                    MultiSelectSongItem(
                        song = song,
                        isSelected = selectedIds.contains(song.uniqueId),
                        isSelectionMode = isSelectionMode,
                        isCurrent = currentSong?.getSongId() == song.songId,
                        onClick = { songsScreenVM.play(song.toMediaItem()) },
                        onMenuClick = {},
                        onToggleSelection = { songsScreenVM.toggleSelection(song.uniqueId) }
                    )
                }
            }
            // 底部操作菜单：仅在选择模式开启且有选中项时显示
            AnimatedVisibility(
                visible = isSelectionMode && selectedIds.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                MultiSelectBottomBar(
                    onDeleteClick = { },
                    onMoveClick = { songsScreenVM.remove() },
                    onAddToClick = { }
                )
            }
        }

    }
}

@Composable
private fun Toolbar(
    isSelectionMode: Boolean,
    clearSelection: () -> Unit,
    allSelection: () -> Unit,
    toggleSelectionMode: () -> Unit,
    refresh: () -> Unit,
    isAllSelection: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isSelectionMode) {
            // 全选 / 全不选
            IconButton(onClick = {
                if (isAllSelection) {
                    clearSelection()
                } else {
                    allSelection()
                }
            }) {
                Icon(
                    if (isAllSelection) {
                        Icons.Default.CheckBox
                    } else {
                        Icons.Default.CheckBoxOutlineBlank
                    },

                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = toggleSelectionMode) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null
                )
            }
        } else {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = toggleSelectionMode) {
                Icon(
                    imageVector = Icons.Default.Checklist,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = refresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )
            }
        }


    }
}

@Composable
private fun CoverSection(
    songList: SongListEntity,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            ImageWidget(
                cover = songList.cover,
                modifier = Modifier.size(150.dp)
            )

            Column() {
                Text(
                    text = songList.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,

                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = songList.createDate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }
}


@Composable
private fun MultiSelectBottomBar(
    onDeleteClick: () -> Unit,
    onMoveClick: () -> Unit,
    onAddToClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true, // 由父组件控制是否显示
        enter = slideInVertically { fullHeight -> fullHeight } + fadeIn(),
        exit = slideOutVertically { fullHeight -> fullHeight } + fadeOut(),
        label = "bottomActionMenu"
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = WindowInsets.navigationBars.getBottom(LocalDensity.current).dp),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomBarItem(
                    icon = Icons.Default.Delete,
                    text = "删除",
                    onClick = onDeleteClick
                )
                BottomBarItem(
                    icon = Icons.Default.ExitToApp,
                    text = "移出",
                    onClick = onMoveClick
                )
                BottomBarItem(
                    icon = Icons.Default.LibraryAdd,
                    text = "添加到",
                    onClick = onAddToClick
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

//@Preview
//@Composable
//fun prevew(){
//    CoverSection(SongListEntity(0,"ceshi", cover = "", count = 1, createDate = "2026-3-12",sort=1))
//}