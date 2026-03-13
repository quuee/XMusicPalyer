package cn.x.ui.screen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.x.data.db.SongEntity
import cn.x.ui.componets.SongItemWidget

/**
 * 用于展示文件夹歌曲 歌单歌曲的页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    naviBack: () -> Unit
) {

    val colorScheme = MaterialTheme.colorScheme
    val listState = rememberLazyListState()

    // 计算顶部区域是否还在可见范围内
    val topSectionHeightDp = with(LocalConfiguration.current) { screenHeightDp.dp / 3 }
    val density = LocalDensity.current

    // 获取顶部区域是否被滚出：如果 firstVisibleItemIndex > 0，说明顶部已完全滚出
    // 如果 index == 0，但 offset > 0，说明正在滚出
    val alpha by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                // 第一项（顶部区域）还在，计算透明度
                val maxOffsetPx = with(density) { topSectionHeightDp.toPx() }
                val progress =
                    (listState.firstVisibleItemScrollOffset / maxOffsetPx).coerceAtMost(1f)
                1f - progress
            } else {
                // 顶部已完全滚出
                0f
            }
        }
    }

    val songs = listOf(
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
        SongEntity(title = "333", artist = "efe", duration = 12345L),
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "song list",
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = naviBack) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "back")
                    }
                },
            )
        },

        ) { padding ->
        // 可滚动内容
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding),

            ) {
            item {
                TopSection(
                    playlistName = "ceshi",
                    createTime = "2026-3-13",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(topSectionHeightDp)
                        .alpha(alpha)
                )
            }

            stickyHeader {
                PlaylistToolbar(
                    playlistName = "ddd",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(colorScheme.secondaryContainer)
                )
            }

            itemsIndexed(songs) { index, song ->
                SongItemWidget(
                    title = song.title,
                    artist = song.artist,
                    duration = song.duration,
                    onClick = {},
                    onMenuClick = {}
                )
            }
        }
    }
}

@Composable
private fun PlaylistToolbar(
    playlistName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = playlistName,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /* 分享 */ }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "分享"
            )
        }
    }
}

@Composable
private fun TopSection(
    playlistName: String,
    createTime: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF4A90E2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = playlistName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = createTime,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.8f)
                )
            )
        }
    }
}