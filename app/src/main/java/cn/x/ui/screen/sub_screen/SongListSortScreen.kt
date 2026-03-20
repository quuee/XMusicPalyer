package cn.x.ui.screen.sub_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.ui.componets.ImageWidget
import kotlin.math.roundToInt

/**
 * 歌单排序
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListSortScreen(
    naviBack: () -> Unit,
    songListSortScreenVM: SongListSortScreenVM = hiltViewModel(),
) {

    val draggingOffset = songListSortScreenVM.draggingOffset.collectAsState()
    val draggingIndex = songListSortScreenVM.draggingIndex.collectAsState()
    val songLists = songListSortScreenVM.songLists.collectAsState()

    LaunchedEffect(Unit) {
        songListSortScreenVM.loadSongLists()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "song list sort",
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

        Box(modifier = Modifier.padding(padding)) {

            LazyColumn {

                itemsIndexed(songLists.value) { index, item ->

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .zIndex(if (draggingIndex.value == index) 1f else 0f)
                            .offset {
                                // 拖动后的偏移量（位置）
                                if (draggingIndex.value == index) {
                                    IntOffset(
                                        draggingOffset.value.x.roundToInt(),
                                        draggingOffset.value.y.roundToInt()
                                    )
                                } else {
                                    IntOffset.Zero
                                }
                            }
                            .onGloballyPositioned { layoutCoordinates ->
                                // 获取项的位置信息
                                val position = layoutCoordinates.positionInWindow()
                                songListSortScreenVM.calculateDeltaY(
                                    index,
                                    position.y,
                                    position.y + layoutCoordinates.size.height
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .background(MaterialTheme.colorScheme.surface),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                ImageWidget(
                                    item.cover,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(MaterialTheme.shapes.small),
                                    contentScale = ContentScale.Crop
                                )

                            }
                            Column(modifier = Modifier.weight(1f)) {
                                // 歌曲信息
                                Text(item.name)
                            }

                            DragHandle(
                                onDragStart = { songListSortScreenVM.startDrag(index) },
                                onDragUpdate = { songListSortScreenVM.updateDrag(it) },
                                onDragEnd = { songListSortScreenVM.finishDrag() },
                                onDragCancel = { songListSortScreenVM.finishDrag() }
                            )

                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun DragHandle(
    onDragStart: () -> Unit,
    onDragUpdate: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDragUpdate(dragAmount)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragCancel() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
//        Icon(modifier = Modifier.align(Alignment.CenterVertically))(
        Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = "拖动排序"
        )
    }
}
