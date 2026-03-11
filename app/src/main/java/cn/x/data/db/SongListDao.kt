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
    fun insertSongList(songList: SongListEntity)

    @Query("SELECT * FROM song_lists")
    fun getAllSongLists(): List<SongListEntity>



    @Insert
    fun addSongToSongList(songlistSong: SongListWithSongEntity)

    @Delete
    fun removeSongFromSongList(songlistSong: SongListWithSongEntity)

    @Transaction
    @Query("SELECT * FROM song_lists WHERE id = :songlistId")
    fun getSongListWithSongs(songlistId: Long): SongListWithSongs?
}