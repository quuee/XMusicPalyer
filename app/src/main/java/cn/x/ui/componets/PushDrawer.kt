package cn.x.ui.componets

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import cn.x.R
import cn.x.route.Routes
import kotlinx.coroutines.launch

@Composable
fun PushDrawer(
    drawerContent: @Composable () -> Unit,
    content: @Composable (drawerControl: DrawerControl) -> Unit,
    drawerWidth: Dp = LocalConfiguration.current.screenWidthDp.dp / 2
) {
    val colorScheme = MaterialTheme.colorScheme

    val density = LocalDensity.current
    val drawerWidthPx = with(density) { drawerWidth.toPx() }

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val isOpen by remember {
        derivedStateOf { offsetX.value > drawerWidthPx / 2 }
    }

    val openDrawer: () -> Unit = {
        scope.launch { offsetX.animateTo(drawerWidthPx, spring()) }
    }

    val closeDrawer: () -> Unit = {
        scope.launch { offsetX.animateTo(0f, spring()) }
    }

    val drawerControl = remember(isOpen) {
        DrawerControl(
            open = openDrawer,
            close = closeDrawer,
            isOpen = isOpen
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // 🟦 Drawer
        Box(
            modifier = Modifier
                .width(drawerWidth)
                .fillMaxHeight()
                .background(colorScheme.background)
        ) {
            drawerContent()
        }

        // 🟥 主内容区域（点击关闭）
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .background(colorScheme.background) // 不能加,会把上面的菜单挡住
                .offset { IntOffset(offsetX.value.toInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val target =
                                    (offsetX.value + dragAmount).coerceIn(0f, drawerWidthPx)
                                offsetX.snapTo(target)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                if (isOpen) offsetX.animateTo(drawerWidthPx, spring())
                                else offsetX.animateTo(0f, spring())
                            }
                        }
                    )
                }
                .clickable(
                    enabled = isOpen,
                    // 不要点击动画涟漪效果
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { closeDrawer() }
        ) {
            if (offsetX.value > 0) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.3f * (offsetX.value / drawerWidthPx)))
                )
            }

            content(drawerControl)
        }
    }
}

class DrawerControl internal constructor(
    val open: () -> Unit,
    val close: () -> Unit,
    val isOpen: Boolean
) {
    fun toggle() {
        if (isOpen) close() else open()
    }
}

// region 侧边栏
enum class DrawerElement(
    val title: Int,
    val icon: Int,
    val index: Int
) {
    ScanLocalSong(
        R.string.drawer_scan_local_song,
        R.drawable.icon_drawer_scan,
//        Routes.Scan
        0
    ),
    Folder(
        R.string.drawer_local_folder,
        R.drawable.icon_drawer_folder,
//        Routes.Folder,
        1
    ),
    LocalSongList(
        R.string.drawer_local_song_list,
        R.drawable.icon_drawer_music_list,
//        Routes.SongList
        2
    ),
    LocalSong(
        R.string.drawer_local_song,
        R.drawable.icon_drawer_song,
//        Routes.LocalSong
        3
    ),
}
// endregion

@Composable
fun DrawerContent(
    elements: Array<DrawerElement> = DrawerElement.entries.toTypedArray(),
    naviRouteItem: (Int) -> Unit,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
//            .background().verticalScroll()
    ) {

        // 头像身份


        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth() // 确保 Row 占满整行宽度
        ) {
            IconButton(
                onClick = {
                    /* 跳转到设置页面 */
                    naviRouteItem(4)
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_drawer_setting),
                    contentDescription = stringResource(R.string.setting)
                )
            }
            IconButton(
                onClick = {},
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_camera_scan),
                    contentDescription = "camera scan"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 菜单区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
//            .heightIn()
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top

        ) {

            itemsIndexed(elements) { index, item ->
                DrawerItem(item, naviRouteItem)
                Spacer(modifier = Modifier.height(8.dp))
            }

        }
    }
}


@SuppressLint("ResourceType")
@Composable
private fun DrawerItem(
    element: DrawerElement, naviRouteItem: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clickable {
                    naviRouteItem(element.index)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = element.icon),
                contentDescription = stringResource(id = element.title),

                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(id = element.title),
                style = MaterialTheme.typography.bodyMedium,

                )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                contentDescription = stringResource(R.string.more),
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp),
            )
        }
    }
}

