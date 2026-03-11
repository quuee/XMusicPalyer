package cn.x.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SongEntity::class, SongListEntity::class, SongListWithSongEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun SongsDao(): SongsDao

    abstract fun SongListDao(): SongListDao
}