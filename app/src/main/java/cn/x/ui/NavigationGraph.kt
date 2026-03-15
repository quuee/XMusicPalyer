package cn.x.ui

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cn.x.ui.screen.AddSelectSongScreen
import cn.x.ui.screen.AppSharedVM
import cn.x.ui.screen.HomeScreen
import cn.x.ui.screen.PlayerScreen
import cn.x.ui.screen.SongListSortScreen
import cn.x.ui.screen.SongsScreen




/**
 * 根路由
 */
@Composable
fun NavigationGraph(
    navHostController: NavHostController,
    startDistance: String
) {

    val appSharedVM: AppSharedVM = hiltViewModel() // 作用域 = Activity
    NavHost(
        navController = navHostController, startDestination = startDistance
    ) {

        composable(Screens.Home.route) {
            HomeScreen(
                naviRouteItem = { routeString -> navHostController.navigate(routeString) },
                appSharedVM = appSharedVM
            )
        }

        composable(Screens.Player.route) {
            PlayerScreen(naviBack = { navHostController.navigateUp() })
        }

        composable(Screens.Songs.route) {
            SongsScreen(
                naviBack = { navHostController.navigateUp() },
                naviRouteItem = { routeString -> navHostController.navigate(routeString) },
                appSharedVM = appSharedVM
            )
        }

        composable(Screens.SongListSort.route) {
            SongListSortScreen(naviBack = { navHostController.navigateUp() })
        }

        composable(Screens.AddSelectSong.route) {
            AddSelectSongScreen(naviBack = { navHostController.navigateUp() },appSharedVM=appSharedVM)
        }

    }
}