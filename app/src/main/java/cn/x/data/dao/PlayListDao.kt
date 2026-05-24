package cn.x.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import cn.x.data.db.PlayListSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlayListSongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<PlayListSongEntity>)

    @Query("SELECT * FROM play_list_songs")
    fun queryAll(): Flow<List<PlayListSongEntity>>

    @Query("SELECT * FROM play_list_songs WHERE unique_id = :uniqueId")
    suspend fun queryByUniqueId(uniqueId: String): PlayListSongEntity?

    @Delete
    suspend fun delete(entity: PlayListSongEntity)

    @Query("DELETE FROM play_list_songs")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(songs: List<PlayListSongEntity>) {
        clear()
        insertAll(songs)
    }
}