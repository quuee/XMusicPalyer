package cn.x.ui.componets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BufferedSlider(
    currentPosition: Float,      // 当前播放位置（0f ~ duration）
    bufferedPosition: Float,     // 已缓冲位置（0f ~ duration）
    duration: Float,             // 总时长
    onSeek: (Float) -> Unit,     // 拖动结束时回调（或实时回调，按需调整）
    modifier: Modifier = Modifier,
) {
    // 归一化到 [0f, 1f]
    val progress = if (duration > 0f) (currentPosition / duration).coerceIn(0f, 1f) else 0f
    val bufferedProgress = if (duration > 0f) (bufferedPosition / duration).coerceIn(0f, 1f) else 0f

    // 自定义颜色：通过 trackColorRange 模拟缓冲层
    val colors = SliderDefaults.colors(
        thumbColor = Color.White,
        activeTrackColor = Color.Transparent, // 主进度由自定义 track 覆盖
        inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
    )

    Box(modifier = modifier.height(32.dp)) {
        // 手动绘制轨道：底层 + 缓冲层 + 播放层
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 14.dp) // 与 thumb 对齐
        ) {
            val strokeWidth = 4.dp.toPx()
            val centerY = size.height / 2

            // 底层轨道（灰色）
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = strokeWidth
            )

            // 缓冲层（浅灰）
            val bufferedWidth = bufferedProgress * size.width
            drawLine(
                color = Color.Gray.copy(alpha = 0.6f),
                start = Offset(0f, centerY),
                end = Offset(bufferedWidth, centerY),
                strokeWidth = strokeWidth
            )

            // 播放进度（白色）
            val progressWidth = progress * size.width
            drawLine(
                color = Color.White,
                start = Offset(0f, centerY),
                end = Offset(progressWidth, centerY),
                strokeWidth = strokeWidth
            )
        }

        // 可拖拽的 Slider（透明轨道，仅用 thumb）
        Slider(
            value = progress,
            onValueChange = { newProgress ->
                // newProgress [0f, 1f]
                onSeek(newProgress)
            },
            colors = colors,
            modifier = Modifier.fillMaxSize(),
            // 可选：启用实时拖动（默认就是）
            interactionSource = remember { MutableInteractionSource() },
            thumb = {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color.White, CircleShape)
                        .offset(x = 0.dp, y = (-6).dp) // 微调垂直对齐
                )
            }
        )
    }
}