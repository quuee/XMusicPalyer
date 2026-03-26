package cn.x.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.x.R
import cn.x.service.PlayState
import cn.x.ui.Screens
import cn.x.ui.componets.DrawerContent
import cn.x.ui.componets.ImageWidget
import cn.x.ui.componets.MarqueeText
import cn.x.ui.componets.PushDrawer

/**
 * 主页面
 */
@Composable
fun HomeScreen(
    homeScreenVM: HomeScreenVM = hiltViewModel(),
    naviRouteItem: (String) -> Unit,
) {

    val controller = homeScreenVM.playerController
    // 1. 直接收集各个 StateFlow
    val currentSong by controller.currentSong.collectAsState()
    val playState by controller.playState.collectAsState()
    val progress by controller.playProgress.collectAsState()
    val buffering by controller.bufferingPercent.collectAsState()
    val playMode by controller.playMode.collectAsState()
    val playlist by controller.playlist.collectAsState()

    // 2. 获取瞬时值 (Duration 不是 Flow，每次重组都会读取最新值，这是安全的)
    val duration = controller.mediaController.duration.coerceAtLeast(0L)

    // 3. 在本地计算派生状态 (Local Derived State)
    val isPlaying = playState == PlayState.Playing
    val songTitle = currentSong?.mediaMetadata?.title?.toString() ?: "未知曲目"
    val songArtist = currentSong?.mediaMetadata?.artist?.toString() ?: "未知艺术家"
    val isPlaylistEmpty = playlist.isEmpty()

    val lastRoute by homeScreenVM.lastTimeRoute.collectAsState()


    val navHostController = rememberNavController()

    var shouldCloseDrawer by remember { mutableStateOf(false) }

    LaunchedEffect(navHostController) {
        navHostController.currentBackStackEntryFlow.collect {
            shouldCloseDrawer = true
        }
    }


    val TAG = "HomeScreen"

    PushDrawer(
        drawerContent = {
            DrawerContent(naviRouteItem = { routeString ->
                homeScreenVM.updateRoute(routeString) // 记录drawer最后一次路由
                navHostController.navigate(routeString) {
                    popUpTo(0) {
                        inclusive = true
                    } // 清除返回栈
                }
            })
        },
        content = { drawerControl ->

            if (shouldCloseDrawer) {
                drawerControl.close()
                shouldCloseDrawer = false
            }

            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    startDestination = lastRoute,
                    navController = navHostController,
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.navigationBars) // 底部避开系统导航键
                        // 关键点：手动为底部留出空间，避免被 FloatingPlayerBar 遮挡
                        // 同时让顶部自然延伸到状态栏下（由 PushDrawer 保证）
                        .padding(
                            bottom = if (currentSong != null) 72.dp else 0.dp // 估算一个底部高度，或使用动态计算
                            // 更精准的做法是使用 WindowInsets 监听，见下方“进阶优化”
                        )

                ) {
                    composable(Screens.Scan.route) {
                        ScanScreen(onDrawerToggle = { drawerControl.toggle() })
                    }
                    composable(Screens.Folder.route) {
                        FolderScreen(
                            onDrawerToggle = { drawerControl.toggle() },
                            naviRouteItem = naviRouteItem,
                        )
                    }
                    composable(Screens.SongList.route) {
                        SongListScreen(
                            onDrawerToggle = { drawerControl.toggle() },
                            naviRouteItem = naviRouteItem,
                        )
                    }
                    composable(Screens.LocalSong.route) {
                        LocalSongScreen(
                            onDrawerToggle = { drawerControl.toggle() },
                            naviRouteItem = naviRouteItem,
                        )
                    }
                    composable(Screens.Setting.route) {
                        SettingScreen(onDrawerToggle = { drawerControl.toggle() })
                    }
                }

                // 直接放置在 Box 的底部
                if (currentSong != null) {
                    FloatingPlayerBar(
                        mediaItem = currentSong,
                        isPlaying = isPlaying,
                        onSongClick = { naviRouteItem(Screens.Player.route) },
                        onNextClick = { controller.next() },
                        onPreviousClick = { controller.prev() },
                        onPlayPauseClick = { controller.playPause() },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            //下面这行避免播放器会被导航键遮挡
                            .windowInsetsPadding(
                                WindowInsets.navigationBars.only(
                                    WindowInsetsSides.Bottom
                                )
                            )
                    )
                }
            }

        }
    )

}

@Composable
fun FloatingPlayerBar(
    mediaItem: MediaItem?,
    isPlaying: Boolean,
    onSongClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp)
            .clickable(onClick = onSongClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            // 歌曲封面
            ImageWidget(
                cover = mediaItem?.mediaMetadata?.artworkUri.toString(),
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
        }

        // 歌曲名(带滚动效果)
        MarqueeText(
            text = mediaItem?.mediaMetadata?.title.toString(),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            ),
            initialDelay = 1000,
            delay = 2000,
            velocity = 40.dp
        )

        // 控制按钮
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            // 上一首按钮
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = stringResource(R.string.play_previous),
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onPreviousClick),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 播放/暂停按钮
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)

            ) {
                IconButton(
                    onClick = onPlayPauseClick
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) stringResource(R.string.pause) else stringResource(
                            R.string.play
                        ),
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            }

            // 下一首按钮
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = stringResource(R.string.play_next),
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onNextClick),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }
}





