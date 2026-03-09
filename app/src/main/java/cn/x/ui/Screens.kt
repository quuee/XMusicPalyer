package cn.x.ui

sealed class Screens(val route: String) {

    // 重新设计界面,进入默认是本地音乐(如果没有连接远程服务器)
    // 所有界面按钮都在左侧抽屉打开
    // 分两部分,一部分是本地音乐:扫描音乐,文件夹,本地歌单,全部歌曲. 在线音乐:连接远程服务(可多账号,可能局域网互联网),在线歌单,在线全部歌单,download

    // 最底下是playbar

    object Home : Screens("Home")

    object Scan : Screens("Scan")
    object Folder : Screens("Folder")
    object LocalSongList : Screens("LocalSongList")
    object LocalSong : Screens("LocalSong")

    object Setting : Screens("Setting")

    object Search : Screens("Search")

    object SongList : Screens("SongList")

    object SongListSort : Screens("SongListSort")
}