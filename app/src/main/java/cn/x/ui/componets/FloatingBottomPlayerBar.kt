package cn.x.ui.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import cn.x.R

@Composable
fun FloatingBottomPlayerBar(
    mediaItem: MediaItem?,
    isPlaying: Boolean,
    onSongClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp)
            .clickable(onClick = onSongClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            // 歌曲封面
            ImageWidget(
                cover = mediaItem?.mediaMetadata?.artworkUri.toString(),
                modifier = Modifier
                    .size(58.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
        }

        // 歌曲名(带滚动效果)
        MarqueeText(
            text = mediaItem?.mediaMetadata?.title.toString(),
//            text = mediaItem?.mediaMetadata?.title?.toString() ?: "未知曲目",
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f),
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            ),
            initialDelay = 1000,
            delay = 1000,
            velocity = 40.dp
        )

        // 控制按钮
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        )
        {
            // 上一首按钮
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = stringResource(R.string.play_previous),
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onPreviousClick),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 播放/暂停按钮
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)

            ) {
                IconButton(
                    onClick = onPlayPauseClick
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) stringResource(R.string.play_pause) else stringResource(
                            R.string.play
                        ),
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

            }

            // 下一首按钮
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = stringResource(R.string.play_next),
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onNextClick),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }
}