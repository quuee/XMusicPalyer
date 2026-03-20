package cn.x.data

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.x.data.dao.FolderDao
import cn.x.data.dao.PlayListDao
import cn.x.data.dao.SongDao
import cn.x.data.dao.SongListDao
import cn.x.data.db.FolderEntity
import cn.x.data.db.PlayListSongEntity
import cn.x.data.db.SongEntity
import cn.x.data.db.SongListEntity
import cn.x.data.db.SongListWithSongEntity

@Database(
    entities = [
        SongEntity::class,
        SongListEntity::class,
        SongListWithSongEntity::class,
        PlayListSongEntity::class,
        FolderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun SongDao(): SongDao

    abstract fun SongListDao(): SongListDao

    abstract fun PlayListDao(): PlayListDao

    abstract fun FolderDao(): FolderDao
}