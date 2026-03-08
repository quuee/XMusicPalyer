package cn.x

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置为全屏沉浸模式
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            XMusicApplicationApp()
        }
    }

}
