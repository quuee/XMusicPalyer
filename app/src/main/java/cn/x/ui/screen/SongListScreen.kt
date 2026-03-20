package cn.x.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.R
import cn.x.ui.Screens
import cn.x.ui.componets.CenterTopBar
import cn.x.ui.componets.FloatingDropdownMenu
import cn.x.ui.componets.SongListItemWidget

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