package cn.x.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 歌单
 */
@Entity(tableName = "song_lists")
data class SongListEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("cover")
    var cover: String,
    @ColumnInfo("count")
    val count: Int,
    @ColumnInfo("create_date")
    val createDate: String
)