package cn.x.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.x.R
import cn.x.ui.Screens
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navHostController = rememberNavController()

    val TAG = "HomeScreen"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    naviRouteItem = { routeString ->
                        // 导航前先关闭抽屉
                        scope.launch {
                            drawerState.close()
                            navHostController.navigate(routeString)
                        }
                    },
                    onLogoutSheet = { }
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    SearchTopBar(
                        onDrawer = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                    Log.d(TAG, "drawerState.open()")
                                } else {
                                    drawerState.close()
                                }
                            }
                        },
                        onSearch = {},
                        onClearClick = {},
                        searchText = ""
                    )

                },
                bottomBar = {

                },

                ) { innerPadding ->
                NavHost(
                    startDestination = Screens.Scan.route,
                    navController = navHostController,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screens.Scan.route) {
                        ScanScreen()
                    }
                    composable(Screens.Folder.route) {
                        FolderScreen()
                    }
                    composable(Screens.LocalSongList.route) {
                        LocalSongListScreen()
                    }
                    composable(Screens.LocalSong.route) {
                        LocalSongScreen()
                    }


                }


            }
        }
    )

}

//侧边栏
enum class DrawerElement(
    @StringRes val title: Int,
    @StringRes val icon: Int,
    val route: String
) {
    ScanLocalSong(
        R.string.drawer_scan_local_song,
        R.drawable.icon_drawer_scan,
        Screens.Scan.route
    ),
    Folder(
        R.string.drawer_local_folder,
        R.drawable.icon_navigation_folder,
        Screens.Folder.route
    ),
    LocalSongList(
        R.string.drawer_local_song_list,
        R.drawable.icon_navigtion_music_list,
        Screens.LocalSongList.route
    ),
    LocalSong(
        R.string.drawer_local_song,
        R.drawable.icon_navigtion_music_list,
        Screens.LocalSong.route
    ),
}

@Composable
private fun DrawerContent(
    elements: Array<DrawerElement> = DrawerElement.entries.toTypedArray(),
    naviRouteItem: (String) -> Unit,
    onLogoutSheet: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.75f)
            .padding(8.dp)
//            .background().verticalScroll()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.End
            ),
            modifier = Modifier
                .fillMaxWidth() // 确保 Row 占满整行宽度
                .padding(end = 16.dp) // 可选：添加内边距
        ) {
            IconButton(
                onClick = {
                    /* 跳转到设置页面 */
                    naviRouteItem(Screens.Setting.route)
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_drawer_setting),
                    contentDescription = ""
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_camera_scan),
                    contentDescription = ""
                )
            }
        }
        // 头像身份
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp), // 自动给子项之间加 24.dp 的间距
        ) {
            Image(
                painter = painterResource(id = R.drawable.music_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "xxxxx",
                    style = MaterialTheme.typography.bodyMedium,

                    )
                Text(
                    text = "管理员",
                    style = MaterialTheme.typography.bodyMedium,

                    )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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
    element: DrawerElement, naviRouteItem: (String) -> Unit
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
                    naviRouteItem(element.route)
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

@Composable
fun SearchTopBar(
    searchText: String,
//    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onDrawer: () -> Unit,
    onSearch: (String) -> Unit,
) {

    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(8.dp)
            .height(56.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            // 返回按钮
            IconButton(onClick = onDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Drawer",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(32.dp)
                )
            }

            // 搜索框
            TextField(
                value = searchText,
                onValueChange = { /**onSearchTextChanged*/ },
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(Unit) {
                        // focusManager在组建内部好像不行，点不到其他地方
//                        focusManager.clearFocus()
                    },
                placeholder = {
                    Text(
                        "Search...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
//                leadingIcon = {
//                    Icon(
//                        Icons.Default.Search,
//                        contentDescription = "Search",
//                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
//                    )
//                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = onClearClick) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch(searchText)
                        focusManager.clearFocus()
                    }
                ),
                shape = RoundedCornerShape(16.dp),

                )

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}