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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.More
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import cn.x.R
import cn.x.service.PlayMode
import cn.x.util.LyricLine
import cn.x.util.LyricUtil.Companion.findCurrentLyricIndex
import cn.x.util.formatTime
import cn.x.util.getDuration
import coil3.compose.AsyncImage
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
    playMode: PlayMode,
    modifier: Modifier = Modifier
) {

    val coroutineScope = rememberCoroutineScope()

    val density = LocalDensity.current
    val thresholdY = with(density) { 150.dp.toPx() }
    val decay = rememberSplineBasedDecay<Float>()

    val translationY = remember { Animatable(0f) }
    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            // 展开状态：只能向下拖拽（正方向），不能向上
            translationY.updateBounds(lowerBound = 0f, upperBound = thresholdY)
            // 如果之前有负偏移，归零
            translationY.snapTo(0f)
        } else {
            // 收起状态：可以向上拖拽展开（负方向），也可以向下拖拽关闭（正方向）
            translationY.updateBounds(lowerBound = -thresholdY, upperBound = thresholdY)
        }
    }

    val draggableState = rememberDraggableState { dragAmount ->
        coroutineScope.launch {
            // 展开状态下，完全忽略向上的拖拽（dragAmount < 0）
            val effectiveDrag = if (isExpanded && dragAmount < 0f) 0f else dragAmount

            // 阻尼效果：越接近边界阻力越大
            val fraction = (translationY.value / thresholdY).absoluteValue.coerceIn(0f, 1f)
            val dampedDrag = effectiveDrag * (1f - fraction)

            translationY.snapTo(translationY.value + dampedDrag)
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
                        // 展开状态下，只有向下拖拽超过阈值才触发状态变更
                        // 非展开状态下，向上拖拽（decayY < 0）超过阈值触发展开
                        val shouldExpand = !isExpanded && decayY < -(thresholdY * 0.5f)
                        val shouldCollapse = isExpanded && decayY > (thresholdY * 0.5f)

                        // 非展开状态下向下滑动超过阈值 → 停止播放
                        val shouldStopPlayback = !isExpanded && decayY > (thresholdY * 0.5f)

                        when {
                            shouldStopPlayback -> {
//                                onReset()
                                return@launch
                            }
                            shouldExpand || shouldCollapse -> {
                                onPlayerExpandedChange(!isExpanded)
                                translationY.animateTo(0f)
                                // bounds 会在 LaunchedEffect(isExpanded) 中自动更新
                            }
                            else -> {
                                // 未达到阈值，回弹到原位
                                translationY.animateTo(0f)
                            }
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
                playMode = playMode
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
    playMode: PlayMode,
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

            // 主题内容
            Column(
                modifier = Modifier
                    .fillMaxWidth().padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 顶部栏 按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
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

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = Icons.Filled.Alarm,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null
                            )
                        }
                        // 播放模式
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = when (playMode) {
                                    PlayMode.Loop -> Icons.Default.Repeat
                                    PlayMode.Shuffle -> Icons.Default.Shuffle
                                    PlayMode.Single -> Icons.Default.RepeatOne
                                },
                                contentDescription = "Playback Mode",
                            )
                        }
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "more"
                            )
                        }
                    }
                }

                // 封面
                CoverLyricsPager(
                    currentTrack,
                    listOf(LyricLine(0L, "no lyric")),
                    progress,
                    isPlaying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(4f)
                )

                // 歌曲信息 进度条
                SongBufferedSlider(
                    progress,
                    0,
                    currentTrack.mediaMetadata.getDuration(),
                    seekTo = { p ->  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                )
                // 控制按钮
                ControlsButton(
                    previous = { onPrevious() },
                    playPause = { onPlayPause() },
                    playNext = { onNext() },
                    isPlaying = isPlaying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }

}

@Composable
private fun CoverLyricsPager(
    currentSong: MediaItem?,
    lyrics: List<LyricLine>,
    currentPosition: Long,
    isPlaying: Boolean,
    modifier: Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 2 })


    // 滑动内容
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        when (page) {
            // 第一页 - 歌曲封面
            0 -> {
                AlbumCover(
                    currentSong?.mediaMetadata?.artworkUri.toString(),
                    currentSong?.mediaMetadata?.title.toString(),
                    currentSong?.mediaMetadata?.artist.toString(),
                    isPlaying = isPlaying
                )
            }

            // 第二页 - 歌词
            1 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    LyricsScroller(lyrics, currentPosition + 200L)
                }
            }
        }
    }
    // 滑动指示器
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pagerState.pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == pagerState.currentPage) Color.White
                        else Color.White.copy(alpha = 0.5f)
                    )
            )
        }
    }

}

@Composable
private fun AlbumCover(artworkUri: String?,title:String?,artist:String?, isPlaying: Boolean) {
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier= Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = artworkUri,
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(300.dp)
                    .graphicsLayer {
                        rotationZ = if (isPlaying) rotation else 0f
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                    }
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.icon_placeholder),
                error = painterResource(R.drawable.logo)
            )
            // 歌曲名
            Text(
                text = title?:"",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            // 歌手
            Text(
                text = artist?:"",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
        }

    }
}


@Composable
private fun LyricsScroller(lyrics: List<LyricLine>, currentPosition: Long) {
    val listState = rememberLazyListState()
    val currentLine = remember(lyrics, currentPosition) {
        findCurrentLyricIndex(lyrics, currentPosition)
    }

    LaunchedEffect(currentLine) {
        if (currentLine >= 0) {
            listState.animateScrollToItem(currentLine)
        }
    }

    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val contentPadding = PaddingValues(vertical = maxHeight / 2)

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = contentPadding
        ) {
            itemsIndexed(lyrics) { index, line ->
                val isCurrentLine = index == currentLine

                Text(
                    text = line.content,
                    style = if (isCurrentLine) typography.headlineMedium else typography.bodyLarge,
                    color = if (isCurrentLine) colorScheme.primary else Color.Gray,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SongBufferedSlider(
    progress: Long,
    buffering: Long,
    duration: Long,
    seekTo: (Long) -> Unit,
    modifier: Modifier
) {
    // 格式化时间
    val formattedProgress = formatTime(progress)
    val formattedDuration = formatTime(duration)


    Column(
        modifier = modifier,
    ) {

        // 进度条
        BufferedSlider(
            currentPosition = progress.toFloat(),
            bufferedPosition = buffering.toFloat(),
            duration = duration.toFloat(),
            onSeek = { newPosition ->
//                Log.d("PlayerScreen", "PlayerScreen newPosition: $newPosition")
                seekTo(newPosition.toLong())
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        // 00:00  04:00
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formattedProgress, color = Color.Gray, fontSize = 12.sp)
            Text(text = formattedDuration, color = Color.Gray, fontSize = 12.sp)
        }
    }


}

@Composable
private fun ControlsButton(
    previous: () -> Unit,
    playPause: () -> Unit,
    playNext: () -> Unit,
    isPlaying: Boolean,
    modifier: Modifier
) {
    // 控制按钮
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {


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
    }
}

@Preview
@Composable
fun PPPP() {

}
