 
使用media3 mediasession架构，要求：
- 1 界面层：compose
- 2 状态管理：viewmodel+单项数据流
- 3 数据层：Flow
- 4 播放逻辑 业务逻辑 数据处理 和 界面分离解藕
- 5 继承MediaSessionService的类
- 6 播放控制类（封装成单例）全局使用
- 7 支持本地+在线播放
- 8 页面路由
工具库：
- 1 dagger hilt
- 2 kotlin协程
- 3 网络与序列化 retrofit kotlin.serialization
- 4 本地数据存储 room


## bug

### 重新插入歌曲，歌单中歌曲消失
@Insert(onConflict = OnConflictStrategy.IGNORE) 暂时解决
### SQLite 默认的 LIKE 运算符对多字节字符（如中文）的支持有限
FTS5 (全文搜索) (适合大量数据)

### 通过mediastore获取歌曲信息为 未知
使用文件名

## 想做的功能 TODO
### 分页加载数据。
### 分页加载数据,播放全部列表歌曲,实现方式：

    用户点击“播放全部”时，只加载第一页（如前 50 首）。
    播放器内部维护一个“待播队列”，初始为第一页。
    当播放进度接近队列末尾（比如还剩 5 首）时，异步请求下一页数据并追加到队列末尾。
    若用户跳到很靠后的歌曲（如第 800 首），则通过歌单总长度和分页大小计算所需页码，直接跳页加载该段数据，并构建局部播放上下文。

### 桌面歌词
### 均衡器
### 在线获取歌曲封面和歌词


## 有用的知识点

### mutableStateOf MutableStateFlow
| 特性 | `mutableStateOf<Long>` | `MutableStateFlow<Long>` |
|------|------------------------|---------------------------|
| 所属生态 | Compose 原生 | Kotlin 协程 / Flow |
| 自动重组 | ✅ 直接支持 | ✅ 需 `.collectAsState()` |
| 支持协程操作 | ❌ | ✅（如 `map`, `combine`, `debounce` 等） |
| 冷流/热流 | 状态持有（类似热） | StateFlow 是热流 |
| 测试友好性 | 一般 | ✅ 可用 `TestStateFlow` 测试 |
| 内存开销 | 极低 | 略高（但可忽略） |

### Room不支持在主线程操作
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
