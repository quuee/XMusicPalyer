package cn.x.ui.screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun LocalSongScreen(
    localSongScreenVM: LocalSongScreenVM= hiltViewModel()
){
    val songList by localSongScreenVM.songList.collectAsState()

    Box() {

        LazyColumn() {
            itemsIndexed(songList) {index,songItem ->
                Row() {
                    Text(songItem.title)
                }
            }
        }
    }
}