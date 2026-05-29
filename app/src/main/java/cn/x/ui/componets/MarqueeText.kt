package cn.x.ui.componets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


/**
 * 自定义实现文字跑马灯
 * Modifier.basicMarquee() 已实现
 */
@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier,
    textStyle: TextStyle = TextStyle.Default,
    delay: Long = 1000,
    initialDelay: Long = 0,
    velocity: Dp = 30.dp
) {
    val density = LocalDensity.current
    var textWidth by remember { mutableFloatStateOf(0f) }
    var containerWidth by remember { mutableFloatStateOf(0f) }
    val animatedOffset = remember { Animatable(0f) }
    val velocityPx = with(density) { velocity.toPx() }

    val needMarquee = remember(textWidth, containerWidth) {
        textWidth > containerWidth && textWidth > 0 && containerWidth > 0
    }

    LaunchedEffect(needMarquee, text) {
        if (!needMarquee) {
            animatedOffset.snapTo(0f)
            return@LaunchedEffect
        }

        delay(initialDelay)

        val scrollDistance = textWidth - containerWidth
        val scrollDuration = (scrollDistance / velocityPx * 1000).toLong()

        while (true) {
            // 从左向右滚动：从 0 到 -scrollDistance
            animatedOffset.animateTo(
                targetValue = -scrollDistance,
                animationSpec = tween(
                    durationMillis = scrollDuration.toInt(),
                    easing = LinearEasing
                )
            )
            delay(delay)

            // 从右向左滚动：从 -scrollDistance 到 0
            animatedOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = scrollDuration.toInt(),
                    easing = LinearEasing
                )
            )
            delay(delay)
        }
    }

    Layout(
        content = {
            Text(
                text = text,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.graphicsLayer {
                    // 使用 graphicsLayer 的 translationX 实现滚动
                    translationX = animatedOffset.value
                }
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds()
            .onSizeChanged { containerWidth = it.width.toFloat() }
    ) { measurables, constraints ->
        val textPlaceable = measurables[0].measure(
            Constraints(
                minWidth = 0,
                maxWidth = Int.MAX_VALUE,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )

        textWidth = textPlaceable.width.toFloat()
        val width = constraints.maxWidth

        layout(width, textPlaceable.height) {
            // 不需要手动设置 x 坐标，因为已经通过 graphicsLayer 的 translationX 处理了
            textPlaceable.placeRelative(x = 0, y = 0)
        }
    }
}