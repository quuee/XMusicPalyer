//package cn.x.di
//
//import cn.x.ui.theme.AppThemeMode
//import cn.x.util.Constants
//import cn.x.util.SPUtil
//import dagger.Module
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
///**
// * 打算用于全局系统配置
// */
//@Module
//@InstallIn(SingletonComponent::class)
//object SettingModule {
//    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
//    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()
//
//
//    fun load() {
//        val themeName = SPUtil.getString(Constants.AppMode)
//        val mode = when (themeName) {
//            AppThemeMode.LIGHT.name -> AppThemeMode.LIGHT
//            AppThemeMode.DARK.name -> AppThemeMode.DARK
//            AppThemeMode.SYSTEM.name -> AppThemeMode.SYSTEM
//            else -> {
//                AppThemeMode.SYSTEM
//            }
//        }
//        _themeMode.value = mode
//    }
//
//    fun saveThemeMode(newThemeMode: AppThemeMode) {
//        _themeMode.value = newThemeMode
//        SPUtil.putString(Constants.AppMode, newThemeMode.name)
//    }
//}