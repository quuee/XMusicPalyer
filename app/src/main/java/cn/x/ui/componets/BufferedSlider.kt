package cn.x.ui.componets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BufferedSlider(
    currentPosition: Float,
    bufferedPosition: Float,
    duration: Float,
    onSeek: (Float) -> Unit, // 这里代表最终确定的 seek 位置
    modifier: Modifier = Modifier,
) {
    val progress = if (duration > 0f) (currentPosition / duration).coerceIn(0f, 1f) else 0f
    val bufferedProgress = if (duration > 0f) (bufferedPosition / duration).coerceIn(0f, 1f) else 0f

    // 关键点：引入一个本地状态来存储拖动时的临时进度
    var draggingProgress by remember { mutableStateOf<Float?>(null) }

    // 决定显示哪个进度：如果在拖动，显示拖动进度；否则显示实际播放进度
    val displayProgress = draggingProgress ?: progress

    val colors = SliderDefaults.colors(
        thumbColor = Color.White,
        activeTrackColor = Color.Transparent,
        inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
    )

    Box(modifier = modifier.height(32.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 14.dp)
        ) {
            val strokeWidth = 4.dp.toPx()
            val centerY = size.height / 2

            // 底层
            drawLine(
                Color.Gray.copy(alpha = 0.3f),
                Offset(0f, centerY),
                Offset(size.width, centerY),
                strokeWidth
            )

            // 缓冲层
            drawLine(
                Color.Gray.copy(alpha = 0.6f),
                Offset(0f, centerY),
                Offset(bufferedProgress * size.width, centerY),
                strokeWidth
            )

            // 播放层 (使用 displayProgress 保证拖动时跟手)
            drawLine(
                Color.White,
                Offset(0f, centerY),
                Offset(displayProgress * size.width, centerY),
                strokeWidth
            )
        }

        Slider(
            value = displayProgress,
            // 1. onValueChange: 仅更新本地 UI 状态，不调用 onSeek (不通知播放器)
            onValueChange = { newValue ->
                draggingProgress = newValue
            },
            // 2. onValueChangeFinished: 手指抬起时，才真正执行 seek
            onValueChangeFinished = {
                draggingProgress?.let {
                    onSeek(it * duration) // 转换回绝对时间
                    draggingProgress = null // 重置，交还给 controller 的状态
                }
            },
            colors = colors,
            modifier = Modifier.fillMaxSize(),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color.White, CircleShape)
                        .offset(y = (-6).dp)
                )
            }
        )
    }
}

