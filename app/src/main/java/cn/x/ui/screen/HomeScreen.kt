package cn.x.ui.screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.x.service.PlayState
import cn.x.ui.Screens
import cn.x.ui.componets.DrawerContent
import cn.x.ui.componets.FloatingBottomPlayerBar
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
                    FloatingBottomPlayerBar(
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







