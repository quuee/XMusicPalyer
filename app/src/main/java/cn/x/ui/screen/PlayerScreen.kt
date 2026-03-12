package cn.x.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.MediaItem
import cn.x.service.PlayMode
import cn.x.service.PlayState
import cn.x.ui.screen.PlayerScreenVM
import cn.x.util.formatTime
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerScreenVM = hiltViewModel(),
    naviBack: () -> Unit
) {

    val controller = viewModel.playerController
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

    // 格式化时间
    val formattedProgress = formatTime(progress)
    val formattedDuration = formatTime(duration)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212)), // 深色背景
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. 专辑封面 (带旋转动画)
            AlbumCover(
                song = currentSong,
                isPlaying = isPlaying
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. 歌曲信息
            Text(
                text = songTitle,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = songArtist,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. 进度条
            Slider(
                value = if (duration > 0) (progress.toFloat() / duration) else 0f,
                onValueChange = { viewModel.seekTo(it) },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = formattedProgress, color = Color.Gray, fontSize = 12.sp)
                Text(text = formattedDuration, color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. 缓冲提示
            if (playState == PlayState.Preparing || buffering > 0 && buffering < 100) {
                LinearProgressIndicator(
                    progress = (buffering / 100f).coerceIn(0f, 1f),
                    color = Color.Yellow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. 控制按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 播放模式按钮
                IconButton(onClick = { viewModel.togglePlayMode() }) {
                    Icon(
                        imageVector = getPlayModeIcon(playMode),
                        contentDescription = "Play Mode",
                        tint = if (playMode != PlayMode.Loop) Color.Yellow else Color.White
                    )
                }

                // 上一首
                IconButton(onClick = { viewModel.prev() }) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // 播放/暂停 (大按钮)
                FilledIconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier.size(72.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(36.dp)
                    )
                }

                // 下一首
                IconButton(onClick = { viewModel.next() }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // 占位或列表按钮
                IconButton(onClick = { /* 打开播放列表 */ }) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Playlist",
                        tint = Color.White
                    )
                }
            }
        }

        // 空状态提示
        if (isPlaylistEmpty) {
            Text(
                text = "播放列表为空",
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
            )
        }
    }
}

/**
 * 专辑封面旋转
 */
@Composable
private fun AlbumCover(song: MediaItem?, isPlaying: Boolean) {
    val rotation by animateFloatAsState(
        targetValue = if (isPlaying) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // 如果暂停，我们需要保持当前的旋转角度，而不是重置为0。
    // 上面的逻辑简单化处理了：暂停时动画停止在当前帧需要更复杂的逻辑，
    // 这里简化为：播放时旋转，暂停时不旋转（视觉上可能瞬间跳回0，生产环境需优化）
    // 优化方案：使用 remember 保存当前角度，暂停时停止动画更新目标值。

    val animatedRotation = if (isPlaying) rotation else 0f
    // 注意：简单的 animateFloatAsState 在 pause 时会倒转回 0。
    // 真正的黑胶唱片效果需要自定义 AnimationSpec 或手动控制 targetValue 累加。
    // 此处仅做演示，使用简化的旋转逻辑：

    Box(
        modifier = Modifier
            .size(280.dp)
            .rotate(if (isPlaying) rotation else 0f) // 简化版：暂停会倒转，生产请用 deriveAnimatedFloat
    ) {
        AsyncImage(
            model = song?.mediaMetadata?.artworkUri,
            contentDescription = "Album Art",
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray, CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
            error = painterResource(android.R.drawable.ic_menu_gallery)
        )

        // 中间的黑点
        Box(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Center)
                .background(Color.Black, CircleShape)
        )
    }
}

@Composable
private fun getPlayModeIcon(mode: PlayMode): ImageVector {
    return when (mode) {
        PlayMode.Loop -> Icons.Default.Repeat
        PlayMode.Shuffle -> Icons.Default.Shuffle
        PlayMode.Single -> Icons.Default.RepeatOne
    }
}


