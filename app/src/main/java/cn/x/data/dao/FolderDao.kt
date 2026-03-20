package cn.x.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.x.data.db.FolderEntity

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(folder: FolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<FolderEntity>)

    @Query("SELECT * FROM folders")
    fun getAllFolders(): List<FolderEntity>

    @Delete
    fun delete(folder: FolderEntity)

    @Query("DELETE FROM folders")
    fun clear()

}