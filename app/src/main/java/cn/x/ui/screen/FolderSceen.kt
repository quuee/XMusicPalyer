package cn.x.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.ui.componets.FolderCard

@Composable
fun FolderScreen(
    folderScreenVM: FolderScreenVM = hiltViewModel()
){
    val colorScheme = MaterialTheme.colorScheme
    val currentFolders by folderScreenVM.currentFolders.collectAsState()

    Box(
        modifier = Modifier.background(colorScheme.primaryContainer)
    ) {
        LazyColumn {
            itemsIndexed(currentFolders) { index: Int, folder ->
                FolderCard(
                    folder.folderPath,
                    folder.songCount,
                    onClick = {} // todo 点击后跳转到LocalSongScreen
                )
            }
        }
    }

}