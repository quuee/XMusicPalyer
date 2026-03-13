package cn.x.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

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