package cn.x.ui.screen

import androidx.lifecycle.ViewModel
import cn.x.service.PlayerController
import cn.x.route.Routes
import cn.x.util.Constants
import cn.x.util.SPUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class HomeScreenVM (
    val playerController: PlayerController
) : ViewModel() {


    // 最后一次路由
    private val _lastTimeRoute = MutableStateFlow<Int>(0)
    val lastTimeRoute: StateFlow<Int> = _lastTimeRoute.asStateFlow()

    init {
        // 协程作用域中读取 (如果 SPUtil 是同步的可以直接读，如果是异步的需 launch)
        // 假设 SPUtil 是同步读取
        val savedRoute = SPUtil.getInt(Constants.LastTimeRoute, 0)
        _lastTimeRoute.value = savedRoute

    }

    fun updateRoute(newRoute: Int) {
        _lastTimeRoute.value = newRoute
        // 立即持久化
        SPUtil.putInt(Constants.LastTimeRoute, newRoute)
    }


}
