package cn.x.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cn.x.route.LocalNavigator
import cn.x.ui.componets.FolderCard
import org.koin.compose.viewmodel.koinViewModel

/**
 * 歌曲文件夹列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(
    folderScreenVM: FolderScreenVM = koinViewModel(),
    onDrawerToggle: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val currentFolders by folderScreenVM.currentFolders.collectAsState()
    val navigator = LocalNavigator.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Folder",
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDrawerToggle) {
                        Icon(Icons.Filled.Menu, contentDescription = "Drawer Menu")
                    }
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(currentFolders) { index: Int, folder ->
                FolderCard(
                    folder.folderPath,
                    folder.songCount,
                    onClick = {}
                )
            }
        }
    }


}