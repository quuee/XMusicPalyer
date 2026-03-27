package cn.x.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 主题模式枚举
enum class AppThemeMode {
    LIGHT,      // 白天模式
    DARK,       // 黑夜模式
    SYSTEM      // 跟随系统
}

// =============== Light Theme (Day) ===============
private val LightColorScheme = lightColorScheme(
    // 背景色：浅灰白，柔和不刺眼
    background = Color(0xFFF8F9FA),          // #F8F9FA - 浅灰背景
    surface = Color(0xFFFFFFFF),             // #FFFFFF - 卡片/容器底色
    onBackground = Color(0xFF1A1C1E),        // #1A1C1E - 主要文字（深灰近黑）
    onSurface = Color(0xFF1A1C1E),           // 同上，用于容器内文字
    surfaceVariant = Color(0xFFE7E8EA),      // #E7E8EA - 次级容器/分隔线
    onSurfaceVariant = Color(0xFF494B4D),    // #494B4D - 次级文字/图标

    // 强调色（用于播放按钮、选中状态、高亮）
    primary = Color(0xFF1DB954),             // Spotify 绿（经典音乐品牌色）
    onPrimary = Color(0xFFFFFFFF),           // 白色文字在强调色上
    primaryContainer = Color(0xFFE0F2E6),    // 强调色浅容器（用于 hover/focus）
    onPrimaryContainer = Color(0xFF006D32),  // 深绿文字用于浅绿容器

    // 错误色（如删除、断开连接等）
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),

    // 阴影与分割线（Material 3 中通常用 surfaceVariant 或 outline）
    outline = Color(0xFF7C7E80),             // 分割线颜色
)

// =============== Dark Theme (Night) ===============
private val DarkColorScheme = darkColorScheme(
    // 背景色：深灰黑，营造沉浸式听歌体验
    background = Color(0xFF121212),          // #121212 - 经典深色背景（Spotify/YouTube Music）
    surface = Color(0xFF1E1E1E),             // #1E1E1E - 卡片/容器底色（略亮于背景）
    onBackground = Color(0xFFE0E0E0),        // #E0E0E0 - 主要文字（浅灰白）
    onSurface = Color(0xFFE0E0E0),           // 容器内文字
    surfaceVariant = Color(0xFF2D2D2D),      // #2D2D2D - 次级容器/分隔区域
    onSurfaceVariant = Color(0xFFB0B0B0),    // #B0B0B0 - 次级文字/图标（柔和不刺眼）

    // 强调色保持一致，确保品牌识别
    primary = Color(0xFF1DB954),             // 保留 Spotify 绿
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF004D26),    // 深绿容器（暗色模式下更协调）
    onPrimaryContainer = Color(0xFF8FF9B0),  // 亮绿文字用于深绿容器

    error = Color(0xFFCF6679),
    onError = Color(0xFF000000),

    outline = Color(0xFF5A5A5A),             // 暗色模式下的分割线
)

@Composable
fun XMusicPlayerTheme(
    themeMode: AppThemeMode,
    content: @Composable () -> Unit
) {

    val darkTheme = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}