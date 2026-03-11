package cn.x.ui.componets

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    delay: Int = 1000, // 开始滚动前的延迟(毫秒)
    initialDelay: Int = 0, // 初始延迟(毫秒)
    velocity: Dp = 30.dp // 滚动速度
) {
    val density = LocalDensity.current
    val textWidth = remember { mutableFloatStateOf(0f) }
    val containerWidth = remember { mutableFloatStateOf(0f) }
    val offsetX = remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    val velocityPx = with(density) { velocity.toPx() }

    // 是否需要滚动(文本宽度大于容器宽度)
    val needMarquee = remember(textWidth.floatValue, containerWidth.floatValue) {
        textWidth.floatValue > containerWidth.floatValue
    }

    LaunchedEffect(needMarquee, text) {
        if (!needMarquee) {
            offsetX.floatValue = 0f
            return@LaunchedEffect
        }

        delay(initialDelay.toLong())

        while (true) {
            // 从右向左滚动
            val duration = ((textWidth.floatValue + offsetX.floatValue) / velocityPx * 1000).toLong()
            offsetX.floatValue = 0f
            coroutineScope.launch {
                animate(
                    initialValue = 0f,
                    targetValue = -textWidth.floatValue,
                    animationSpec = tween(duration.toInt(), easing = LinearEasing),
                ) { value, _ ->
                    offsetX.floatValue = value
                }
            }

            // 等待滚动完成+延迟
            delay(duration + delay)

            // 从左向右滚动
            val returnDuration = (textWidth.floatValue / velocityPx * 1000).toLong()
            coroutineScope.launch {
                animate(
                    initialValue = -textWidth.floatValue,
                    targetValue = 0f,
                    animationSpec = tween(returnDuration.toInt(), easing = LinearEasing),
                ) { value, _ ->
                    offsetX.floatValue = value
                }
            }

            // 等待返回完成+延迟
            delay(returnDuration + delay)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (needMarquee) Modifier else Modifier),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .onSizeChanged { containerWidth.floatValue = it.width.toFloat() }
                .clipToBounds()
        ) {
            Text(
                text = text,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier
                    .offset(x = with(density) { offsetX.floatValue.toDp() })
                    .onSizeChanged { textWidth.floatValue = it.width.toFloat() }
            )
        }
    }
}