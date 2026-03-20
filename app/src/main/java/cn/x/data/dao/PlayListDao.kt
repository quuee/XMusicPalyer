package cn.x.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.x.data.db.PlayListSongEntity

@Dao
interface PlayListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: PlayListSongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<PlayListSongEntity>)

    @Query("SELECT * FROM play_list_songs")
    fun queryAll(): List<PlayListSongEntity>

    @Query("SELECT * FROM play_list_songs WHERE unique_id = :uniqueId")
    fun queryByUniqueId(uniqueId: String): PlayListSongEntity?

    @Delete
    fun delete(entity: PlayListSongEntity)

    @Query("DELETE FROM play_list_songs")
    fun clear()
}