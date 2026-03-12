package cn.x.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)

//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)

// 清新风格的配色
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),      // 清新绿
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF087F23),

    secondary = Color(0xFF81D4FA),     // 清新蓝
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFB3E5FC),
    onSecondaryContainer = Color(0xFF0288D1),

    background = Color(0xFFF5F9F5),
    onBackground = Color(0xFF121212),// 文字颜色
    surface = Color(0xFFE8F5E9),
    onSurface = Color(0xFF2E2E2E),

    error = Color(0xFFEF5350),
    onError = Color.White,
    errorContainer = Color(0xFFFADBD8),
    onErrorContainer = Color(0xFFC62828)
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),      // 柔和绿
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFC8E6C9),

    secondary = Color(0xFF4FC3F7),    // 柔和蓝
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0277BD),
    onSecondaryContainer = Color(0xFFB3E5FC),

    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),

    error = Color(0xFFEF9A9A),
    onError = Color.Black,
    errorContainer = Color(0xFFC62828),
    onErrorContainer = Color(0xFFFADBD8)
)

@Composable
fun XMusicPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}