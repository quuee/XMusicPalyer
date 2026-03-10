package cn.x.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SongsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: SongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<SongEntity>)

    @Query("SELECT * FROM songs")
    fun queryAll(): List<SongEntity>

    @Query("SELECT * FROM songs WHERE unique_id = :uniqueId")
    fun queryByUniqueId(uniqueId: String): SongEntity?

    @Delete
    fun delete(entity: SongEntity)

    @Query("DELETE FROM songs")
    fun clear()
}