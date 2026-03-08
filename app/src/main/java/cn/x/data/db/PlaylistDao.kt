package cn.x.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: SongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<SongEntity>)

    @Query("SELECT * FROM play_list")
    fun queryAll(): List<SongEntity>

    @Query("SELECT * FROM play_list WHERE unique_id = :uniqueId")
    fun queryByUniqueId(uniqueId: String): SongEntity?

    @Delete
    fun delete(entity: SongEntity)

    @Query("DELETE FROM play_list")
    fun clear()
}