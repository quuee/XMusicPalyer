package cn.x.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cn.x.ui.screen.sub_screen.AddSelectSongScreen
import cn.x.ui.screen.sub_screen.FolderSongsScreen
import cn.x.ui.screen.HomeScreen
import cn.x.ui.screen.sub_screen.PlayerScreen
import cn.x.ui.screen.sub_screen.SearchScreen
import cn.x.ui.screen.sub_screen.SongListSortScreen
import cn.x.ui.screen.sub_screen.SongsScreen
import cn.x.util.Constants


/**
 * 根路由
 * 参数不允许带 '/'
 */
@Composable
fun NavigationGraph(
    navHostController: NavHostController,
    startDistance: String
) {

    NavHost(
        navController = navHostController, startDestination = startDistance
    ) {

        composable(Screens.Home.route) {
            HomeScreen(
                naviRouteItem = { routeString -> navHostController.navigate(routeString) },
            )
        }

        composable(Screens.Player.route) {
            PlayerScreen(naviBack = { navHostController.navigateUp() })
        }

        composable(
            route = Screens.Songs.route.plus("/{${Constants.SongListId}}"),
            arguments = listOf(
                navArgument(name = "songListId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )) { backStackEntry ->
            val songListId = backStackEntry.arguments?.getLong(Constants.SongListId) ?: 0L
            SongsScreen(
                naviBack = { navHostController.navigateUp() },
                naviRouteItem = { routeString -> navHostController.navigate(routeString) },
                songListId = songListId
            )
        }

        composable(
            route = Screens.FolderSongs.route.plus("?${Constants.FolderPath}={${Constants.FolderPath}}"),
            arguments = listOf(
                navArgument(name = Constants.FolderPath) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )) { backStackEntry ->
            val folderPath = backStackEntry.arguments?.getString(Constants.FolderPath) ?: ""
            FolderSongsScreen(
                naviBack = { navHostController.navigateUp() },
                path = folderPath
            )
        }

        composable(Screens.SongListSort.route) {
            SongListSortScreen(naviBack = { navHostController.navigateUp() })
        }

        composable(
            route = Screens.AddSelectSong.route.plus("/{${Constants.SongListId}}"),
            arguments = listOf(
                navArgument(name = Constants.SongListId) {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )) { backStackEntry ->
            val songListId = backStackEntry.arguments?.getLong(Constants.SongListId) ?: 0L
            AddSelectSongScreen(
                naviBack = { navHostController.navigateUp() },
                songListId = songListId
            )
        }

        composable(
            route = Screens.Search.route,
        ) {
            SearchScreen(naviBack = { navHostController.navigateUp() })
        }

    }
}