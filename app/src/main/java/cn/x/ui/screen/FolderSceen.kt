package cn.x.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cn.x.ui.Screens
import cn.x.ui.componets.CenterTopBar
import cn.x.ui.componets.FolderCard
import cn.x.util.Constants
import org.koin.compose.viewmodel.koinViewModel

/**
 * 歌曲文件夹列表
 */
@Composable
fun FolderScreen(
    folderScreenVM: FolderScreenVM = koinViewModel(),
    onDrawerToggle: () -> Unit,
    naviRouteItem: (String) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val currentFolders by folderScreenVM.currentFolders.collectAsState()

    Scaffold(
        topBar = {
            CenterTopBar(
                "Folder",
                onDrawerToggle,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(currentFolders) { index: Int, folder ->
                FolderCard(
                    folder.folderPath,
                    folder.songCount,
                    onClick = {naviRouteItem(Screens.FolderSongs.route.plus("?${Constants.FolderPath}=${folder.folderPath}"))}
                )
            }
        }
    }


}