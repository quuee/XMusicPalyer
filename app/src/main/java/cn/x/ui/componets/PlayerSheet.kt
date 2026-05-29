package cn.x.ui.componets

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.media3.common.MediaItem
import cn.x.R
import cn.x.service.PlayMode
import cn.x.service.PlayState
import cn.x.service.PlaybackState
import cn.x.service.PlayerController
import cn.x.util.getDuration
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun PlayerSheet(
    isExpanded: Boolean,// 展开 或 缩小
    onPlayerExpandedChange: (Boolean) -> Unit,
    isPlaying: Boolean,
    currentTrack: MediaItem,
    progress: Long,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {

    val coroutineScope = rememberCoroutineScope()

    val density = LocalDensity.current
    val thresholdY = with(density) { 150.dp.toPx() }
    val decay = rememberSplineBasedDecay<Float>()

    val translationY = remember {
        Animatable(0f).apply {
            if (isExpanded) {
                updateBounds(
                    lowerBound = 0f,
                    upperBound = thresholdY
                )
            } else {
                updateBounds(
                    lowerBound = -thresholdY,
                    upperBound = thresholdY
                )
            }
        }
    }
    val draggableState = rememberDraggableState { dragAmount ->
        coroutineScope.launch {
            translationY.snapTo(
                translationY.value + (dragAmount * (1 - (translationY.value / thresholdY).absoluteValue))
            )
        }
    }

    AnimatedContent(
        targetState = isExpanded,
        label = "player-content",
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.translationY = translationY.value

                val cornerRadius =
                    lerp(0.dp, 36.dp, this.translationY.absoluteValue / (thresholdY * 0.5f))
                this.clip = true
                this.shape = if (isExpanded) {
                    RoundedCornerShape(cornerRadius)
                } else {
                    RoundedCornerShape(
                        topStart = 36.dp,
                        topEnd = 36.dp,
                        bottomStart = cornerRadius,
                        bottomEnd = cornerRadius
                    )
                }
            }
            .draggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    val decayY = decay.calculateTargetValue(
                        initialValue = translationY.value,
                        initialVelocity = velocity
                    )

                    coroutineScope.launch {
                        if (!isExpanded) {
                            val shouldStopPlayback = decayY > thresholdY * .5f
                            if (shouldStopPlayback) {
//                                onReset()
                                return@launch
                            }
                        }

                        val shouldChangeExpandedState = decayY.absoluteValue > (thresholdY * 0.5f)
                        if (shouldChangeExpandedState) {
                            onPlayerExpandedChange(!isExpanded)
                            translationY.apply {
                                animateTo(0f)
                                if (isExpanded) {
                                    updateBounds(
                                        lowerBound = 0f,
                                        upperBound = thresholdY
                                    )
                                } else {
                                    updateBounds(
                                        lowerBound = -thresholdY,
                                        upperBound = thresholdY
                                    )
                                }
                            }
                        } else {
                            translationY.animateTo(0f)
                        }
                    }
                }
            )
            .background(color = MaterialTheme.colorScheme.surfaceContainer)

    ) { state ->
        when (state) {
            false -> BottomPlayer(
                isPlaying = isPlaying,
                currentTrack = currentTrack,
                onOpenClick = {
                    onPlayerExpandedChange(true)
                    translationY.updateBounds(
                        lowerBound = 0f,
                        upperBound = thresholdY
                    )
                },
                progress = progress,
                onPlayPause = onPlayPause,
                onPrevious = onPrevious,
                onNext = onNext,
                modifier = Modifier
            )

            true -> ExpandedPlayer(
                currentTrack = currentTrack,
                onHideClick = {
                    onPlayerExpandedChange(false)
                    translationY.updateBounds(
                        lowerBound = -thresholdY,
                        upperBound = 0f
                    )
                },
                progress = progress,
                onPlayPause = onPlayPause,
                onPrevious = onPrevious,
                onNext = onNext,
                isPlaying = isPlaying,
            )


        }
    }
}

@Composable
private fun BottomPlayer(
    isPlaying: Boolean,
    currentTrack: MediaItem,
    onOpenClick: () -> Unit,
    progress: Long,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable {
                onOpenClick()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(
                    animateFloatAsState(
                        // 播放进度
                        targetValue = (progress.toFloat() / currentTrack.mediaMetadata.getDuration()),
                        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
                        label = "bottom-player-progress"
                    ).value
                )
                .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 当封面和歌名改变时用动画过渡
            AnimatedContent(
                targetState = currentTrack,
                transitionSpec = {
                    fadeIn() + slideInHorizontally(
                        initialOffsetX = { it / 5 }
                    ) togetherWith fadeOut() + slideOutHorizontally(
                        targetOffsetX = { -it / 5 }
                    )
                },
                label = "bottom-player-track-change-animation"
            ) { currentTrack ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                ) {
                    ImageWidget(
                        cover = currentTrack.mediaMetadata.artworkUri.toString(),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = currentTrack.mediaMetadata.title.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.basicMarquee()
                        )

                        Text(
                            text = currentTrack.mediaMetadata.artist.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.basicMarquee(),

                            )
                    }
                }
            }

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
                        .size(28.dp)
                        .clickable(
                            onClick = onPrevious
                        ),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 播放/暂停按钮
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)

                ) {
                    IconButton(
                        onClick = onPlayPause
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) stringResource(R.string.play_pause) else stringResource(
                                R.string.play
                            ),
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                }

                // 下一首按钮
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = stringResource(R.string.play_next),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(
                            onClick = onNext
                        ),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ExpandedPlayer(
    currentTrack: MediaItem,
    onHideClick: () -> Unit,
    isPlaying: Boolean,
    progress: Long,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    // 返回手势
    BackHandler {
        onHideClick()
    }
    Box {
        // 背景模糊效果
        ImageWidget(
            cover = currentTrack.mediaMetadata.artworkUri.toString(),
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 50.dp),
            contentScale = ContentScale.Crop
        )
        // 半透明遮罩
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {

            // 顶部栏 按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .align(Alignment.TopCenter),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 关闭按钮
                IconButton(
                    onClick = onHideClick
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ExpandMore,
                        contentDescription = "close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                        contentDescription = null
                    )
                }
            }

            // 主题内容
            Column(
                modifier = Modifier
                    .fillMaxWidth().padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                // 封面
                AlbumCover(currentTrack.mediaMetadata.artworkUri.toString(), isPlaying = true)
                // 歌曲信息

                // 进度条
                BufferedSlider(
                    currentPosition = progress.toFloat(),
                    bufferedPosition = 0f,
                    duration = currentTrack.mediaMetadata.getDuration().toFloat(),
                    onSeek = { newPosition ->
//                        seekTo(newPosition.toLong())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                // 控制按钮
                ControlsButton(
                    previous = { onPrevious() },
                    playPause = { onPlayPause() },
                    playNext = { onNext() },
                    togglePlayMode = {  },
                    playList = { },
                    isPlaying = isPlaying,
                    playMode= PlayMode.Loop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }

}

@Composable
private fun AlbumCover(artworkUri: String?, isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = artworkUri,
            contentDescription = "Album Art",
            modifier = Modifier
                .size(240.dp)
                .graphicsLayer {
                    rotationZ = if (isPlaying) rotation else 0f
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                }
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.icon_placeholder),
            error = painterResource(R.drawable.logo)
        )
    }

}

@Composable
private fun ControlsButton(
    previous: () -> Unit,
    playPause: () -> Unit,
    playNext: () -> Unit,
    togglePlayMode: () -> Unit,
    playList: () -> Unit,
    isPlaying: Boolean,
    playMode: PlayMode,
    modifier: Modifier
) {
    // 控制按钮
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 播放模式
        IconButton(onClick = { togglePlayMode() }) {
            Icon(
                imageVector = when (playMode) {
                    PlayMode.Loop -> Icons.Default.Repeat
                    PlayMode.Shuffle -> Icons.Default.Shuffle
                    PlayMode.Single -> Icons.Default.RepeatOne
                },
                contentDescription = "Playback Mode",
                tint = Color.White
            )
        }

        // 上一首
        IconButton(
            onClick = { previous() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        // 播放/暂停
        IconButton(
            onClick = { playPause() },
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause
                else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        // 下一首
        IconButton(
            onClick = { playNext() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        // 播放列表
        IconButton(onClick = playList) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "playlist",
                tint = Color.White
            )
        }
    }
}

@Preview
@Composable
fun PPPP() {

//    PlayerSheet(isExpanded = false, onPlayerExpandedChange = {}, currentTrack = MediaItem())
}
