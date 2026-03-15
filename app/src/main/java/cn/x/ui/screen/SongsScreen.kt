package cn.x.ui.screen

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Share
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
import cn.x.ui.Screens
import cn.x.ui.componets.ImageWidget
import cn.x.ui.componets.MultiSelectSongItem


/**
 * 用于展示文件夹歌曲 歌单歌曲的页面
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    songsScreenVM: SongsScreenVM = hiltViewModel(),
    naviBack: () -> Unit,
    naviRouteItem: (String) -> Unit,
) {

    val items by songsScreenVM.items.collectAsState()
    val selectedIds by songsScreenVM.selectedIds.collectAsState()
    val isSelectionMode by songsScreenVM.isSelectionMode.collectAsState()

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


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "song list",
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = naviBack) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "back")
                    }
                },
                actions = {
                    if (!isSelectionMode) {
                        IconButton(onClick = {naviRouteItem(Screens.AddSelectSong.route)}) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    }

                    if (isSelectionMode) {
                        IconButton(onClick = { songsScreenVM.clearSelection() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                    IconButton(onClick = { songsScreenVM.toggleSelectionMode() }) {
                        Icon(
                            imageVector = if (isSelectionMode) Icons.Default.Done else Icons.Default.Checklist,
                            contentDescription = if (isSelectionMode) "Done" else "Select"
                        )
                    }
                }
            )
        },

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
                        SongListEntity(
                            0,
                            "ceshi",
                            cover = "",
                            count = 1,
                            createDate = "2026-3-12",
                            sort = 1
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(topSectionHeightDp)
                            .alpha(alpha)
                    )
                }

                stickyHeader {
                    Toolbar(
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
                        onClick = {},
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
                    onMoveClick = { },
                    onAddToClick = { }
                )
            }
        }

    }
}

@Composable
private fun Toolbar(

    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.RestoreFromTrash,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /* 分享 */ }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "分享"
            )
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