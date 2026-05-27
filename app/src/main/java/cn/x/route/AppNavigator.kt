package cn.x.route

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

// ✅ 1. 定义导航契约
interface AppNavigator {

    // 基础栈操作（可选暴露，用于通用返回）
    fun popBack()

    fun navigateToSongListDetail(songListId: Long)
    fun navigateToAddSelectSong(songListId: Long)


    fun navigate(route: NavKey)

}

// ✅ 2. 基于 NavBackStack 的实现
class AppNavigatorImpl(
    private val backStack: NavBackStack<NavKey>
) : AppNavigator {

    override fun navigateToSongListDetail(songListId: Long) {
        backStack.add(Routes.Songs(songListId))
    }

    override fun navigateToAddSelectSong(songListId: Long) {
        backStack.add(Routes.AddSelectSong(songListId))
    }

    override fun navigate(route: NavKey) {
        backStack.add(route)
    }

//    override fun openProfile(userId: String) {
//        // 示例：如果需要清除中间页再跳转
//        backStack.removeAll { it is Routes.Profile }
//        backStack.add(Routes.Profile(userId))
//    }

    override fun popBack() {
        backStack.removeLastOrNull()
    }
}