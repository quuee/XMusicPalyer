package cn.x.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.x.ui.screen.PlayerScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            // 这里可以是 HomeScreen 或直接导航到 PlayerScreen
            PlayerScreen()
        }
        composable("player") {
            PlayerScreen()
        }
        // ... 其他页面
    }
}