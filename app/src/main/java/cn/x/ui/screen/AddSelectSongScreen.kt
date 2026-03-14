package cn.x.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.ui.componets.MultiSelectSongItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSelectSongScreen(
    naviBack: () -> Unit,
    addSelectSongScreenVM: AddSelectSongScreenVM = hiltViewModel()
) {

    val items by addSelectSongScreenVM.items.collectAsState()
    val selectedIds by addSelectSongScreenVM.selectedIds.collectAsState()

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
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = null)
                    }
                }

            )
        }
    ) { paddingValues ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            LazyColumn() {
                itemsIndexed(items) { index, song ->
                    MultiSelectSongItem(
                        song = song,
                        isSelected = selectedIds.contains(song.uniqueId),
                        isSelectionMode = true,
                        onClick = {},
                        onMenuClick = {},
                        onToggleSelection = { addSelectSongScreenVM.toggleSelection(song.uniqueId) }
                    )
                }
            }
        }
    }
}