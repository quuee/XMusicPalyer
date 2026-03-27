package cn.x.ui.screen

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import cn.x.ui.theme.AppThemeMode
import cn.x.util.Constants
import cn.x.util.SPUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingScreenVM @Inject constructor(
    private val spUtil: SPUtil,
) : ViewModel() {

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()


    init {
        val themeName = spUtil.getString(Constants.AppMode)
        val mode = when(themeName){
            AppThemeMode.LIGHT.name -> AppThemeMode.LIGHT
            AppThemeMode.DARK.name -> AppThemeMode.DARK
            AppThemeMode.SYSTEM.name -> AppThemeMode.SYSTEM
            else -> {AppThemeMode.SYSTEM}
        }
        _themeMode.value = mode
    }

    // 保存主题模式
    fun saveThemeMode(mode: AppThemeMode) {
        spUtil.putString(Constants.AppMode,mode.name)
        _themeMode.value = mode
        // 立即应用新设置的主题
        applyThemeMode(mode)
    }

    // 应用主题模式
    private fun applyThemeMode(mode: AppThemeMode) {
        val nightMode = when (mode) {
            AppThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AppThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AppThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        // TODO 不起作用
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}