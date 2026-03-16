用ai生成android音乐app。  
使用media3 mediasession架构，要求：
- 1 界面层：compose
- 2 状态管理：viewmodel+单项数据流
- 3 数据层：Flow
- 4 播放逻辑 业务逻辑 数据处理 和 界面分离解藕
- 5 继承MediaSessionService的类需要完善，添加注释。
- 6 播放控制类（封装成单例）全局使用。
- 7 支持本地+在线播放
- 8 页面路由
工具库：
- 1 dagger hilt
- 2 kotlin协程
- 3 网络与序列化 retrofit kotlin.serialization
- 4 本地数据存储 room

根据以上要求列出项目结构，生成核心代码和播放实例。  

## bug
### mediastore 扫描歌曲扫不全，只能扫几首

### android 11-16 界面不适配，比如状态栏

### 重新插入歌曲，歌单中歌曲消失



## mutableStateOf MutableStateFlow
| 特性 | `mutableStateOf<Long>` | `MutableStateFlow<Long>` |
|------|------------------------|---------------------------|
| 所属生态 | Compose 原生 | Kotlin 协程 / Flow |
| 自动重组 | ✅ 直接支持 | ✅ 需 `.collectAsState()` |
| 支持协程操作 | ❌ | ✅（如 `map`, `combine`, `debounce` 等） |
| 冷流/热流 | 状态持有（类似热） | StateFlow 是热流 |
| 测试友好性 | 一般 | ✅ 可用 `TestStateFlow` 测试 |
| 内存开销 | 极低 | 略高（但可忽略） |

## Room不支持在主线程操作
```kt
    viewModelScope.launch {
        val songListWithSongs = withContext(Dispatchers.IO) { // 在异步中更新数据,ui不会根据数据更新
            db.SongListDao().getSongsBySongListId(songListId) 
        }
        _songs.value = songListWithSongs?.songs?:emptyList() // 在 Main 线程更新
        _songList.value = songListWithSongs?.songList?: EMPTY
    }

```

##  Kotlin
```kt
    var songList by mutableStateOf(EMPTY)
        private set

    //Kotlin 在编译为 JVM 字节码时，会自动为 var 生成一个 setter 方法，其 JVM 方法名恰好就是 setSongList(SongListEntity)
    fun updateSongList(newSongList: SongListEntity) {
        songList = newSongList
//    }
```
