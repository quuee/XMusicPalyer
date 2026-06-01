package cn.x.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import cn.x.ui.screen.HomeScreen
import cn.x.ui.screen.sub_screen.AddSelectSongScreen
import cn.x.ui.screen.sub_screen.PlayerScreen
import cn.x.ui.screen.sub_screen.SearchScreen
import cn.x.ui.screen.sub_screen.SongListSortScreen
import cn.x.ui.screen.sub_screen.SongsScreen



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