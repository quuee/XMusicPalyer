package cn.x.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cn.x.ui.screen.HomeScreen
import cn.x.ui.screen.PlayerScreen
import cn.x.ui.screen.SongsScreen

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

        composable(Screens.Player.route){
            PlayerScreen(naviBack = { navHostController.navigateUp() },)
        }

        composable(Screens.Songs.route){
            SongsScreen(naviBack = { navHostController.navigateUp() },)
        }

    }
}