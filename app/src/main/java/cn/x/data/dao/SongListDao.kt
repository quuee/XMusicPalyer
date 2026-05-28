package cn.x.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import cn.x.data.db.SongListEntity
import cn.x.data.db.SongListWithSongEntity
import cn.x.data.db.SongListWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface SongListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongList(songList: SongListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongListAll(list: List<SongListEntity>)

    @Query("SELECT * FROM song_lists where id = :songListId")
    suspend fun getSongList(songListId:Long): SongListEntity

    @Query("SELECT * FROM song_lists order by sort")
    fun getAllSongLists(): Flow<List<SongListEntity>>

    @Update
    suspend fun updateSongList(entity: SongListEntity)

    @Update
    suspend fun updateSongListAll(list: List<SongListEntity>)

    @Delete
    suspend fun delete(entity: SongListEntity)

    @Query("DELETE FROM song_lists")
    suspend fun clear()

    // 关联表操作
    @Insert(onConflict= OnConflictStrategy.IGNORE)
    suspend fun insertSongsToSongList(songs: List<SongListWithSongEntity>)

    @Delete
    suspend fun deleteSongFromSongList(songlistSongs: List<SongListWithSongEntity>)

    @Transaction
    @Query("SELECT * FROM song_lists WHERE id = :songListId")
    suspend fun getSongsBySongListId(songListId: Long): SongListWithSongs?

    @Query("SELECT count(song_id) FROM songlist_song WHERE songlist_id = :songListId")
    suspend fun getSongsCountBySongListId(songListId: Long):Int

    @Query("DELETE FROM songlist_song WHERE songlist_id = :songListId")
    suspend fun deleteAllBySongListId(songListId: Long)

}