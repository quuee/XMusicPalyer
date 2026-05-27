package cn.x.ui.screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.x.route.LocalNavigator
import cn.x.service.PlayState
import cn.x.route.Routes
import cn.x.ui.componets.DrawerContent
import cn.x.ui.componets.FloatingBottomPlayerBar
import cn.x.ui.componets.PushDrawer
import org.koin.compose.viewmodel.koinViewModel

/**
 * 主页面
 */
@Composable
fun HomeScreen(
    homeScreenVM: HomeScreenVM = koinViewModel(),
) {
    val navigator = LocalNavigator.current
    val controller = homeScreenVM.playerController
    // 1. 直接收集各个 StateFlow
    val currentSong by controller.currentSong.collectAsState()
    val playState by controller.playState.collectAsState()
    val buffering by controller.bufferingPercent.collectAsState()
    val playMode by controller.playMode.collectAsState()
    val playlist by controller.playlist.collectAsState()

    // 2. 获取瞬时值 (Duration 不是 Flow，每次重组都会读取最新值，这是安全的)
    val duration = controller.mediaController.duration.coerceAtLeast(0L)

    // 3. 在本地计算派生状态 (Local Derived State)
    val isPlaying = playState == PlayState.Playing
    // 用于记录选中的底部导航项
    val lastRoute by homeScreenVM.lastTimeRoute.collectAsState()

    // true 关闭Drawer
    var shouldCloseDrawer by remember { mutableStateOf(false) }


    PushDrawer(
        drawerContent = {
            DrawerContent(naviRouteItem = { index ->
                homeScreenVM.updateRoute(index) // 记录drawer最后一次路由
                shouldCloseDrawer = true
            })
        },
        content = { drawerControl ->

            if (shouldCloseDrawer) {
                drawerControl.close()
                shouldCloseDrawer = false
            }

            Box(modifier = Modifier.fillMaxSize()) {

                when (lastRoute) {
                    // 用数字的话，这里顺序需要和drawer里菜单顺序对应里
                    0 -> ScanScreen(onDrawerToggle = { drawerControl.toggle() })
                    1 -> FolderScreen(onDrawerToggle = { drawerControl.toggle() })
                    2 -> SongListScreen(onDrawerToggle = { drawerControl.toggle() })
                    3 -> LocalSongScreen(onDrawerToggle = { drawerControl.toggle() })
                    4 -> SettingScreen(onDrawerToggle = { drawerControl.toggle() })
                }

                // 直接放置在 Box 的底部
                if (currentSong != null) {
                    FloatingBottomPlayerBar(
                        mediaItem = currentSong,
                        isPlaying = isPlaying,
                        onClick = { navigator.navigate(Routes.Player) },
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

