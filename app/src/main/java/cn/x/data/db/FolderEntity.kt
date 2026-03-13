package cn.x.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class FolderEntity(

    @PrimaryKey
    @ColumnInfo("folder_path")
    val folderPath: String,
    @ColumnInfo("song_count")
    val songCount: Int
)