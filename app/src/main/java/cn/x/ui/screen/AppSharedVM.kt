package cn.x.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cn.x.data.db.SongListEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 创建一个共享的viewModel 传递和共享状态/数据
 */
@HiltViewModel
class AppSharedVM @Inject constructor() : ViewModel() {


    /**
     * | 特性 | `mutableStateOf<Long>` | `MutableStateFlow<Long>` |
     * |------|------------------------|---------------------------|
     * | 所属生态 | Compose 原生 | Kotlin 协程 / Flow |
     * | 自动重组 | ✅ 直接支持 | ✅ 需 `.collectAsState()` |
     * | 支持协程操作 | ❌ | ✅（如 `map`, `combine`, `debounce` 等） |
     * | 冷流/热流 | 状态持有（类似热） | StateFlow 是热流 |
     * | 测试友好性 | 一般 | ✅ 可用 `TestStateFlow` 测试 |
     * | 内存开销 | 极低 | 略高（但可忽略） |
     *
     */
    private val EMPTY = SongListEntity(0L, "", "", 0, "", -1)

    var songList by mutableStateOf(EMPTY)
        private set

    //Kotlin 在编译为 JVM 字节码时，会自动为 var 生成一个 setter 方法，其 JVM 方法名恰好就是 setSongList(SongListEntity)
    fun updateSongList(newSongList: SongListEntity) {
        songList = newSongList // ✅ 完全替换
    }
}