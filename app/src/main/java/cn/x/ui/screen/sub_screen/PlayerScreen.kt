package cn.x.ui.screen.sub_screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.MediaItem
import cn.x.R
import cn.x.service.PlayMode
import cn.x.service.PlayState
import cn.x.ui.componets.BufferedSlider
import cn.x.ui.componets.ImageWidget
import cn.x.util.LyricLine
import cn.x.util.LyricUtil.Companion.findCurrentLyricIndex
import cn.x.util.formatTime
import coil3.compose.AsyncImage

/**
 * 播放页面
 */
@Composable
fun PlayerScreen(
    playerScreenVM: PlayerScreenVM = hiltViewModel(),
    naviBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val controller = playerScreenVM.playerController
    // 1. 直接收集各个 StateFlow
    val currentSong by controller.currentSong.collectAsState()
    val playState by controller.playState.collectAsState()
    val progress by controller.playProgress.collectAsState()
    val buffering by controller.bufferingPercent.collectAsState()
    val playMode by controller.playMode.collectAsState()
    val playlist by controller.playlist.collectAsState()

    // 2. 获取瞬时值 (Duration 不是 Flow，每次重组都会读取最新值，这是安全的)
    val duration = controller.mediaController.duration.coerceAtLeast(0L)

    // 3. 在本地计算派生状态 (Local Derived State)
    val isPlaying = playState == PlayState.Playing
    val songTitle = currentSong?.mediaMetadata?.title?.toString() ?: "未知曲目"
    val songArtist = currentSong?.mediaMetadata?.artist?.toString() ?: "未知艺术家"
    val isPlaylistEmpty = playlist.isEmpty()


    Box(
        modifier = Modifier
            .fillMaxSize()
    )
    {
        // 背景模糊效果
        ImageWidget(
            cover = currentSong?.mediaMetadata?.artworkUri.toString(),
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 50.dp),
            contentScale = ContentScale.Crop
        )
        // 半透明遮罩
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .windowInsetsPadding(WindowInsets.statusBars) // 顶部避开状态栏
        ) {

            // 内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 顶部工具栏
                TopBar(naviBack)

                Spacer(modifier = Modifier.height(8.dp))

                // 封面 lyric 滑动区域
                CoverLyricsPager(
                    currentSong,
                    listOf(LyricLine(0L, "no lyric")),
                    progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f)
                )


                // 歌曲信息 播放进度
                SongBufferedSlider(
                    currentSong,
                    progress,
                    buffering,
                    duration,
                    seekTo = { p -> playerScreenVM.seekTo(p) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                )


                // 控制按钮
                ControlsButton(
                    previous = { playerScreenVM.prev() },
                    playPause = { playerScreenVM.togglePlayPause() },
                    playNext = { playerScreenVM.next() },
                    togglePlayMode = { playerScreenVM.togglePlayMode() },
                    isPlaying,
                    playMode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    naviBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { naviBack() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Text(
            text = "正在播放",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = { /* 更多选项 */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun CoverLyricsPager(
    currentSong: MediaItem?,
    lyrics: List<LyricLine>,
    currentPosition: Long,
    modifier: Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 2 })


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
                    isPlaying = true
                )
            }

            // 第二页 - 歌词
            1 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    LyricsScroller(lyrics, currentPosition + 500L)
                }
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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
            error = painterResource(R.drawable.music_logo)
        )
    }
}


@Composable
private fun LyricsScroller(lyrics: List<LyricLine>, currentPosition: Long) {
    val listState = rememberLazyListState()
    val currentLine = remember(lyrics, currentPosition) {
        findCurrentLyricIndex(lyrics, currentPosition)
    }

    // 自动滚动到当前行并居中
    LaunchedEffect(currentLine) {
        if (currentLine >= 0) {
            listState.animateScrollToItem(
                index = currentLine,
                scrollOffset = 0,
            )
        }
    }

    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(lyrics) { index, line ->
            Text(
                text = line.content,
                style = typography.bodyLarge,
                color = if (index == currentLine) colorScheme.primary else Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun SongBufferedSlider(
    currentSong: MediaItem?,
    progress: Long,
    buffering: Int,
    duration: Long,
    seekTo: (Long) -> Unit,
    modifier: Modifier
) {
    // 格式化时间
    val formattedProgress = formatTime(progress)
    val formattedDuration = formatTime(duration)


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                // 歌曲名
                Text(
                    text = currentSong?.mediaMetadata?.title.toString(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                // 歌手
                Text(
                    text = currentSong?.mediaMetadata?.artist.toString(),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }

            IconButton(
                onClick = { /* 切换收藏状态 */ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (currentSong?.mediaMetadata?.extras?.getBoolean("starred") == true) Icons.Outlined.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (currentSong?.mediaMetadata?.extras?.getBoolean("starred") == true) Color.Red else Color.White
                )
            }
        }

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
            modifier = Modifier.fillMaxWidth(),
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
    togglePlayMode: () -> Unit,
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
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.Equalizer,
                contentDescription = "Equalizer",
                tint = Color.White
            )
        }
    }
}