package cn.x.ui.screen

import androidx.lifecycle.ViewModel
import cn.x.service.PlayerController
import cn.x.ui.Screens
import cn.x.util.Constants
import cn.x.util.SPUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeScreenVM @Inject constructor(
    val playerController: PlayerController,
) : ViewModel() {


    private val _lastTimeRoute = MutableStateFlow<String>(Screens.Scan.route)
    val lastTimeRoute: StateFlow<String> = _lastTimeRoute.asStateFlow()

    init {
        // 协程作用域中读取 (如果 SPUtil 是同步的可以直接读，如果是异步的需 launch)
        // 假设 SPUtil 是同步读取
        val savedRoute = SPUtil.getString(Constants.LastTimeRoute, Screens.Scan.route)
        _lastTimeRoute.value = savedRoute
    }

    fun updateRoute(newRoute: String) {
        _lastTimeRoute.value = newRoute
        // 立即持久化
        SPUtil.putString(Constants.LastTimeRoute, newRoute)
    }
}