package cn.x.ui.screen.sub_screen

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.x.data.MusicDatabase
import cn.x.data.dao.SongListDao
import cn.x.data.db.SongListEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections


class SongListSortScreenVM (
    private val songListDao: SongListDao,
) : ViewModel() {
    private val TAG = "SongListSortVM"

    private val _songLists = MutableStateFlow<List<SongListEntity>>(emptyList())
    val songLists = _songLists.asStateFlow()


    // 拖动项索引
    val draggingIndex = MutableStateFlow(-1)

    // 偏移量
    val draggingOffset = MutableStateFlow(Offset.Zero)

    fun loadSongLists() {
        viewModelScope.launch {
            songListDao.getAllSongLists().collect { items->
                _songLists.value = items
            }
        }
    }

    fun startDrag(index: Int) {
//        draggingIndex.intValue = index
        draggingIndex.value = index
    }

    // 更新拖动位置
    fun updateDrag(offset: Offset) {
        Log.d(TAG, "offset.x: ${offset.x} ,offset.y:${offset.y}")
        val current = draggingOffset.value
        draggingOffset.value = Offset(
            x = current.x + offset.x,
            y = current.y + offset.y // 往上减，往下加
        )

//      draggingOffset.update { it + offset } // 简写
    }

    // 计算和交换位置
    fun calculateDeltaY(
        someoneIndex: Int, // 某项索引
        someoneTopY: Float,// 某项头部Y坐标
        someoneBottomY: Float //某项底部Y坐标
    ) {

        val threshold = 1.2

        // 通过计算，得出要交换的目标位置
        if (draggingOffset.value.y > ((someoneBottomY - someoneTopY) / threshold)) {
            // 和此元素的下面元素交换
            if (someoneIndex == songLists.value.size - 1) {
                return
            }
            Log.d(TAG, "向下移动：${draggingOffset.value.y}")
            val currentList = songLists.value.toMutableList()
            Collections.swap(currentList, someoneIndex, someoneIndex + 1)

            _songLists.value = currentList

            draggingOffset.update { Offset.Zero }
            draggingIndex.value = someoneIndex + 1
        }

        if (draggingOffset.value.y < -((someoneBottomY - someoneTopY) / threshold)) {
            // 和和此元素的上面元素交换
            if (someoneIndex == 0) {
                return
            }
            Log.d(TAG, "向上移动：${draggingOffset.value.y}")
            val currentList = songLists.value.toMutableList()
            Collections.swap(currentList, someoneIndex, someoneIndex - 1)

            _songLists.value = currentList

            draggingOffset.update { Offset.Zero }
            draggingIndex.value = someoneIndex - 1
        }
    }

    fun finishDrag() {
        resetState()

        viewModelScope.launch {
            val updatedList = _songLists.value.mapIndexed { index, item ->
                item.copy(sort = index)
            }
            songListDao.updateSongListAll(updatedList)
            _songLists.value = updatedList // 触发 Compose 重组
        }

    }

    // 重置状态
    private fun resetState() {
        draggingIndex.value = -1
        draggingOffset.update { Offset.Zero }
    }
}