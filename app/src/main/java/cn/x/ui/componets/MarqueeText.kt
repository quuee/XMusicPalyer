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
import kotlin.math.roundToInt


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
            animatedOffset.animateTo(
                targetValue = -scrollDistance,
                animationSpec = tween(
                    durationMillis = scrollDuration.toInt(),
                    easing = LinearEasing
                )
            )
            delay(delay)

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

    // 使用 Layout 来完全控制测量和放置
    Layout(
        content = {
            Text(
                text = text,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier
                    .graphicsLayer {
                        translationX = animatedOffset.value
                    }
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds()
            .onSizeChanged { containerWidth = it.width.toFloat() }
    ) { measurables, constraints ->
        // 关键：测量文本时不限制最大宽度
        val textPlaceable = measurables[0].measure(
            Constraints(
                minWidth = 0,
                maxWidth = Int.MAX_VALUE,  // 不限制最大宽度
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )

        // 更新文本宽度
        textWidth = textPlaceable.width.toFloat()

        // 容器宽度使用父布局给的约束
        val width = constraints.maxWidth
        layout(width, textPlaceable.height) {
            textPlaceable.placeRelative(
                x = animatedOffset.value.roundToInt(),
                y = 0
            )
        }
    }
}