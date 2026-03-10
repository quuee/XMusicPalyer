package cn.x.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface SongListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongList(songList: SongListEntity)

    @Query("SELECT * FROM song_lists")
    suspend fun getAllSongLists(): List<SongListEntity>



    @Insert
    suspend fun addSongToSongList(songlistSong: SongListWithSongEntity)

    @Delete
    suspend fun removeSongFromSongList(songlistSong: SongListWithSongEntity)

    @Transaction
    @Query("SELECT * FROM song_lists WHERE id = :songlistId")
    suspend fun getSongListWithSongs(songlistId: Long): SongListWithSongs?
}