package cn.x.data.db


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.x.util.generateUniqueId

/**
 * 播放列表的 每次播放新的列表都更新
 */
@Entity("play_list_songs")
data class PlayListSongEntity(

    // 歌曲类型:本地/网络
    @ColumnInfo("type")
    val type: Int = 0,

    // 歌曲ID
    @ColumnInfo("song_id")
    val songId: Long = 0,

    // 音乐标题
    @ColumnInfo("title")
    val title: String = "",

    // 艺术家
    @ColumnInfo("artist")
    val artist: String = "",

    // 艺术家ID
    @ColumnInfo("artist_id")
    val artistId: Long = 0,

    // 专辑
    @ColumnInfo("album")
    val album: String = "",

    // 专辑ID
    @ColumnInfo("album_id")
    val albumId: Long = 0,

    // 专辑封面
    @ColumnInfo("artwork_uri")
    var artworkUri: String = "",

    // 持续时间
    @ColumnInfo("duration")
    val duration: Long = 0,

    // 播放地址
    @ColumnInfo("uri", defaultValue = "")
    var uri: String = "",

    // [本地]文件路径
    @ColumnInfo("path")
    val path: String = "",

    // [本地]文件名
    @ColumnInfo("file_name")
    val fileName: String = "",

    // [本地]文件大小
    @ColumnInfo("file_size")
    val fileSize: Long = 0,

    // 本地路径 拼接后可播放的uri
//    @ColumnInfo("content_uri")
//    var contentUri: String = "",

    // 本地 父级目录
    @ColumnInfo("parent_folder")
    val parentFolder: String = "",

//    @ColumnInfo("lyrics")
//    var lyrics:String="",

    @PrimaryKey
    @ColumnInfo("unique_id")
    val uniqueId: String = generateUniqueId(type, songId)

)