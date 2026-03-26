package cn.x.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.R
import cn.x.data.db.SongListEntity
import cn.x.ui.Screens
import cn.x.ui.componets.CenterTopBar
import cn.x.ui.componets.FloatingDropdownMenu
import cn.x.ui.componets.ImageWidget

/**
 * 歌单列表
 */
@Composable
fun SongListScreen(
    songListScreenVM: SongListScreenVM = hiltViewModel(),
    onDrawerToggle: () -> Unit,
    naviRouteItem: (String) -> Unit,
) {
    val songLists by songListScreenVM.songLists.collectAsState()
    val showCreateDialog by songListScreenVM.showCreateDialog.collectAsState()
    val newSongListName by songListScreenVM.newSongListName.collectAsState()


    LaunchedEffect(Unit) {
        songListScreenVM.loadSongLists()
    }

    Scaffold(
        topBar = {
            CenterTopBar(
                title = "SongList",
                drawerToggle = onDrawerToggle,
                actions = {
                    Actions(
                        onCreateClick = {songListScreenVM.openCreateDialog()},
                        naviRouteItem = { naviRouteItem(Screens.SongListSort.route) },
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(songLists) { index, songListItem ->
                SongListItemWidget(
                    songListItem,
                    onClick = {
                        naviRouteItem(Screens.Songs.route.plus("/${songListItem.id}"))
                    })
            }
        }

        // 创建歌单的弹窗
        if (showCreateDialog) {
            CreateSongListDialog(
                initialName = newSongListName,
                onNameChange = { songListScreenVM.onNewSongListNameChange(it) },
                onConfirm = {
                    songListScreenVM.createSongListConfirm()
                },
                onDismiss = {
                    songListScreenVM.dismissCreateDialog()
                }
            )
        }
    }
}

@Composable
private fun Actions(onCreateClick: () -> Unit, naviRouteItem: () -> Unit) {
    FloatingDropdownMenu { onDismiss ->
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(R.string.createSongList),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                onCreateClick()
                onDismiss() // 关闭菜单
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Create,
                    null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        )
        HorizontalDivider()
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(R.string.importSongList),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                /* 处理点击 */
                onDismiss()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.ImportExport,
                    null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        )
        HorizontalDivider()
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(R.string.sortSongList),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                naviRouteItem()
                onDismiss()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Sort,
                    null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        )

    }
}

@Composable
private fun CreateSongListDialog(
    initialName: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.createSongList)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { onNameChange(it); name = it },
                label = { Text(stringResource(R.string.songListName)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { if (name.isNotBlank()) onConfirm() }
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm()
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun SongListItemWidget(
    songList: SongListEntity,
    onClick: () -> Unit,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧封面图片
            Box(contentAlignment = Alignment.Center) {
                ImageWidget(
                    songList.cover,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 中间内容区域
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = songList.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.songListCount, songList.count),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            FloatingDropdownMenu {
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(R.string.rename),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = { },
                    leadingIcon = {
                        Icon(
                            Icons.Default.DriveFileRenameOutline,
                            null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    },

                    )
                HorizontalDivider()
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(R.string.exportSongList),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = { /* 处理点击 */ },
                    leadingIcon = {
                        Icon(
                            Icons.Default.ImportExport,
                            null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(R.string.deleteSongList),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = { },
                    leadingIcon = {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.padding(end = 8.dp))
                    }
                )
            }
        }
    }
}