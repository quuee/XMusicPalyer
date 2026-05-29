package cn.x.ui.screen.sub_screen


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import cn.x.R
import cn.x.route.LocalNavigator
import cn.x.service.PlayMode
import cn.x.service.PlayState
import cn.x.ui.componets.BufferedSlider
import cn.x.ui.componets.ImageWidget
import cn.x.ui.componets.MarqueeText
import cn.x.util.LyricLine
import cn.x.util.LyricUtil.Companion.findCurrentLyricIndex
import cn.x.util.formatTime
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel


/**
 * 播放页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    playerScreenVM: PlayerScreenVM = koinViewModel(),
) {

    val navigator = LocalNavigator.current
    val controller = playerScreenVM.playerController
    // 1. 直接收集各个 StateFlow
    val currentSong by controller.currentSong.collectAsState()
    val playState by controller.playState.collectAsState()
    val playProgress by controller.playProgress.collectAsState()
    val buffering by controller.bufferingPercent.collectAsState()
    val playMode by controller.playMode.collectAsState()
    val playlist by controller.playlist.collectAsState()


    // 2. 获取瞬时值 (Duration 不是 Flow，每次重组都会读取最新值，这是安全的)
    val duration = controller.mediaController.duration.coerceAtLeast(0L)

    // 3. 在本地计算派生状态 (Local Derived State)
    val isPlaying = playState == PlayState.Playing

    val lyrics by playerScreenVM.lyrics.collectAsState()

    val verticalPagerState = rememberPagerState(pageCount = { 2 })


    VerticalPager(state = verticalPagerState) { pageIndex ->
        when (pageIndex) {
            0 -> {
                MainContent(
                    naviBack = {navigator.popBack()},
                    currentSong=currentSong,
                    lyrics,
                    playProgress=playProgress,
                    buffering = buffering,
                    duration,
                    isPlaying = isPlaying,
                    playMode=playMode,
                    seekTo = { playerScreenVM.seekTo(it) },
                    prev = { playerScreenVM.prev() },
                    togglePlayMode = { playerScreenVM.togglePlayMode() },
                    next = { playerScreenVM.next() },
                    togglePlayPause = { playerScreenVM.togglePlayPause() },
                )
            }

            1 -> {
                PlayListContent(
                    playList = playlist,
                    currentSongId = currentSong?.mediaId ?: "",
                    onClick = {playerScreenVM.play(it)},
                    )
            }
        }
    }


}

@Composable
private fun MainContent(
    naviBack: () -> Unit,
    currentSong: MediaItem?,
    lyrics: List<LyricLine>,
    playProgress: Long,
    buffering: Long,
    duration: Long,
    isPlaying: Boolean,
    playMode: PlayMode,
    seekTo: (Long) -> Unit,
    prev: () -> Unit,
    togglePlayPause: () -> Unit,
    next: () -> Unit,
    togglePlayMode: () -> Unit,
) {

    Box(modifier = Modifier.fillMaxSize()) {
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
                    .padding(8.dp)
                //手势检测遮罩区域 - 用于从底部向上滑动打开 Sheet
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 顶部工具栏
                TopBar(naviBack, title = currentSong?.mediaMetadata?.title.toString())

                Spacer(modifier = Modifier.height(8.dp))

                // 封面 lyric 滑动区域
                CoverLyricsPager(
                    currentSong,
                    lyrics,
                    playProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f)
                )


                // 歌曲信息 播放进度
                SongBufferedSlider(
                    currentSong,
                    playProgress,
                    buffering,
                    duration,
                    seekTo = { p -> seekTo(p) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                )


                // 控制按钮
                ControlsButton(
                    previous = { prev() },
                    playPause = { togglePlayPause() },
                    playNext = { next() },
                    togglePlayMode = { togglePlayMode() },
                    playList = { },
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
    naviBack: () -> Unit,
    title: String
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

        MarqueeText(
            text = title,
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

        IconButton(onClick = { /* 更多选项 */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.more),
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
                    LyricsScroller(lyrics, currentPosition + 200L)
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
            error = painterResource(R.drawable.logo)
        )
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
    currentSong: MediaItem?,
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

@Composable
private fun PlayListContent(
    playList: List<MediaItem>,
    currentSongId: String,
    onClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // 顶部拖拽区域 - 独立手势处理
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.small
            ) {}
        }

        Text(
            text = "播放队列",
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Text(
            text = "↓ 向下滑动关闭 ↓",
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        // LazyColumn 区域 - 使用嵌套滚动
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()

        ) {
            itemsIndexed(playList) { _, item ->
                ListItem(
                    headlineContent = {
                        Text(
                            item.mediaMetadata.title.toString(),
                            color = if (currentSongId == item.mediaId) MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.7f
                            ) else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.7f
                            ),
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).clickable(onClick={onClick(item.mediaId)})
                )
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            }
        }
    }
}

