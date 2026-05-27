package cn.x.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import cn.x.ui.screen.HomeScreen
import cn.x.ui.screen.LocalSongScreen
import cn.x.ui.screen.sub_screen.AddSelectSongScreen
import cn.x.ui.screen.sub_screen.PlayerScreen
import cn.x.ui.screen.sub_screen.SearchScreen
import cn.x.ui.screen.sub_screen.SongListSortScreen
import cn.x.ui.screen.sub_screen.SongsScreen


/**
 * 根路由
 * 参数不允许带 '/'
 */
//@Composable
//fun NavigationGraph(
//    navHostController: NavHostController,
//    startDistance: String
//) {
//
//    NavHost(
//        navController = navHostController, startDestination = startDistance
//    ) {
//
//        composable(Routes.Home.route) {
//            HomeScreen(
//                naviRouteItem = { routeString -> navHostController.navigate(routeString) },
//            )
//        }
//
//        composable(Routes.Player.route) {
//            PlayerScreen(naviBack = { navHostController.navigateUp() })
//        }
//
//        composable(
//            route = Routes.Songs.route.plus("/{${Constants.SongListId}}"),
//            arguments = listOf(
//                navArgument(name = Constants.SongListId) {
//                    type = NavType.LongType
//                    defaultValue = 0L
//                }
//            )) { backStackEntry ->
//            val songListId = backStackEntry.arguments?.getLong(Constants.SongListId) ?: 0L
//            SongsScreen(
//                naviBack = { navHostController.navigateUp() },
//                naviRouteItem = { routeString -> navHostController.navigate(routeString) },
//                songListId = songListId
//            )
//        }
//
//        composable(
//            route = Routes.FolderSongs.route.plus("?${Constants.FolderPath}={${Constants.FolderPath}}"),
//            arguments = listOf(
//                navArgument(name = Constants.FolderPath) {
//                    type = NavType.StringType
//                    defaultValue = ""
//                }
//            )) { backStackEntry ->
//            val folderPath = backStackEntry.arguments?.getString(Constants.FolderPath) ?: ""
//            FolderSongsScreen(
//                naviBack = { navHostController.navigateUp() },
//                path = folderPath
//            )
//        }
//
//        composable(Routes.SongListSort.route) {
//            SongListSortScreen(naviBack = { navHostController.navigateUp() })
//        }
//
//        composable(
//            route = Routes.AddSelectSong.route.plus("/{${Constants.SongListId}}"),
//            arguments = listOf(
//                navArgument(name = Constants.SongListId) {
//                    type = NavType.LongType
//                    defaultValue = 0L
//                }
//            )) { backStackEntry ->
//            val songListId = backStackEntry.arguments?.getLong(Constants.SongListId) ?: 0L
//            AddSelectSongScreen(
//                naviBack = { navHostController.navigateUp() },
//                songListId = songListId
//            )
//        }
//
//        composable(
//            route = Routes.Search.route,
//        ) {
//            SearchScreen(naviBack = { navHostController.navigateUp() })
//        }
//
//    }
//}

// 使用 CompositionLocal 避免层层传递
val LocalNavigator = staticCompositionLocalOf<AppNavigator> {
    error("Navigator not provided! Wrap your app with ProvideNavigator.")
}

@Composable
fun NavigationGraph() {
    val backStack = rememberNavBackStack(Routes.Home)
    val navigator = remember(backStack) { AppNavigatorImpl(backStack) }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        NavDisplay(
            backStack = backStack,
            entryProvider = entryProvider {
                entry<Routes.Home> {
                    HomeScreen()
                }
                entry<Routes.Songs> { key ->
                    SongsScreen(
                        songListId = key.songListId,
                        )
                }
                entry<Routes.AddSelectSong> { key ->
                    AddSelectSongScreen(
                        songListId = key.songListId,
                    )
                }
                entry<Routes.Player> {
                    PlayerScreen()
                }
                entry<Routes.Search> {
                    SearchScreen()
                }
                entry<Routes.SongListSort> {
                    SongListSortScreen()
                }
            }
        )
    }


}