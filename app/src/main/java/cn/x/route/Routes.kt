package cn.x.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Routes {


    @Serializable
    object Home : NavKey
    @Serializable
    object Scan : NavKey
    @Serializable
    object Folder : NavKey
    @Serializable
    object FolderSongs : NavKey
    @Serializable
    object LocalSong : NavKey
    @Serializable
    object SongList : NavKey
    @Serializable
    object Setting : NavKey
    @Serializable
    object Player : NavKey
    @Serializable
    data class Songs(val songListId: Long) : NavKey
    @Serializable
    data class AddSelectSong(val songListId: Long) : NavKey

    @Serializable
    object Search : NavKey
    @Serializable
    object SongListSort : NavKey
}