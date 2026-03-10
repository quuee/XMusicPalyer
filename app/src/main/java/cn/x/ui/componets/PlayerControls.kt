package cn.x.ui.componets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.x.util.formatTime
import java.util.concurrent.TimeUnit

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    currentPosition: Long,
    duration: Long
) {
    val durationFormatted = remember(duration) { formatTime(duration) }
    val positionFormatted = remember(currentPosition) { formatTime(currentPosition) }

    var sliderPosition by remember(currentPosition, duration) {
        mutableFloatStateOf(if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 时间显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = positionFormatted)
            Text(text = durationFormatted)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 进度条
        Slider(
            value = sliderPosition,
            onValueChange = { newValue ->
                sliderPosition = newValue
            },
            onValueChangeFinished = {
                val newPositionMs = (sliderPosition * duration).toLong()
                onSeek(newPositionMs)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 控制按钮
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onPrevious) {
                Icon(imageVector = Icons.Default.FastRewind, contentDescription = "Previous")
            }
            Spacer(modifier = Modifier.width(32.dp))
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            Spacer(modifier = Modifier.width(32.dp))
            IconButton(onClick = onNext) {
                Icon(imageVector = Icons.Default.FastForward, contentDescription = "Next")
            }
        }
    }
}
