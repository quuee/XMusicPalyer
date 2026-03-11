package cn.x.ui.screen

import androidx.lifecycle.ViewModel
import cn.x.service.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenVM @Inject constructor(
    val playerController: PlayerController // 注入单例控制器
) : ViewModel() {

}