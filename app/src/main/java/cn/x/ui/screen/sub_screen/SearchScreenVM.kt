package cn.x.ui.screen.sub_screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.MusicDatabase
import cn.x.data.db.SongEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchScreenVM @Inject constructor(
    private val db: MusicDatabase,
) : ViewModel() {

    private val _songs = MutableStateFlow<List<SongEntity>>(emptyList())
    val songs: StateFlow<List<SongEntity>> = _songs.asStateFlow()

    private val _searchWord = MutableStateFlow<String?>(null)
    val searchWord = _searchWord.asStateFlow()

    fun changeSearchWord(word:String?){
        _searchWord.value = word

        viewModelScope.launch {
            val songs = withContext(Dispatchers.IO) {
                // 查询歌单歌曲
                db.SongDao().queryLike(word)
            }
            _songs.value = songs
        }
    }
}