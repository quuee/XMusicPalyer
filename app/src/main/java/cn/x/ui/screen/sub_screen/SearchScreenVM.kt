package cn.x.ui.screen.sub_screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.dao.SongDao
import cn.x.data.db.SongEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class SearchScreenVM(
    private val songDao: SongDao,
) : ViewModel() {

    private val _songs = MutableStateFlow<List<SongEntity>>(emptyList())
    val songs: StateFlow<List<SongEntity>> = _songs.asStateFlow()

    private val _searchWord = MutableStateFlow<String?>(null)
    val searchWord = _searchWord.asStateFlow()

    fun changeSearchWord(word:String?){
        _searchWord.value = word

        viewModelScope.launch {
            songDao.queryLike(word).collect { songs->
                _songs.value = songs
            }
        }
    }
}