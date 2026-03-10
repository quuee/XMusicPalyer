package cn.x.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation


@Entity(
    tableName = "songlist_song",
    primaryKeys = ["songlist_id", "song_id"],
    foreignKeys = [
        ForeignKey(
            entity = SongListEntity::class,
            parentColumns = ["id"],
            childColumns = ["songlist_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["unique_id"],
            childColumns = ["song_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SongListWithSongEntity(
    @ColumnInfo("songlist_id")
    val songlistId: Long,
    @ColumnInfo("song_id")
    val songId: Long
)


data class SongListWithSongs(
    @Embedded val songList: SongListEntity,
    @Relation(
        parentColumn = "id", // 👉 对应 Songlist 表中的 `id` 字段
        entityColumn = "unique_id", // 👉 对应 Song 表中的 `id` 字段
        associateBy = Junction(
            value = SongListWithSongEntity::class,
            parentColumn = "songlist_id", // 中间表 → 指向 Songlist.id
            entityColumn = "song_id" // 中间表 → 指向 Song.id
        )
    )
    val songs: List<SongEntity>
)