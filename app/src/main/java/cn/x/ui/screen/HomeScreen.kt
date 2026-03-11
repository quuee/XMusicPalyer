package cn.x.ui.screen


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.x.ui.Screens
import cn.x.ui.componets.CenterTopBar
import cn.x.ui.componets.DrawerContent
import cn.x.ui.componets.PushDrawer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

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
                navHostController.navigate(
                    routeString
                )
            }, onLogoutSheet = { })
        },
        content = { drawerControl ->

            if (shouldCloseDrawer) {
                drawerControl.close()
                shouldCloseDrawer = false
            }
            // 监听当前路由变化，并从中提取标题
            val currentRoute = navHostController
                .currentBackStackEntryFlow
                .collectAsState(initial = navHostController.currentBackStackEntry)
                .value
                ?.destination
                ?.route

            Scaffold(
                topBar = {
                    CenterTopBar(currentRoute, drawerToggle = { drawerControl.toggle() })
                },
                bottomBar = {

                },

                ) { innerPadding ->
                NavHost(
                    startDestination = Screens.Scan.route,
                    navController = navHostController,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screens.Scan.route) {
                        ScanScreen()
                    }
                    composable(Screens.Folder.route) {
                        FolderScreen()
                    }
                    composable(Screens.LocalSongList.route) {
                        LocalSongListScreen()
                    }
                    composable(Screens.LocalSong.route) {
                        LocalSongScreen()
                    }
                }
            }
        }
    )

}





