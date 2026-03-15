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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSongListAll(list: List<SongListEntity>)

    @Query("SELECT * FROM song_lists order by sort")
    fun getAllSongLists(): List<SongListEntity>

    @Delete
    fun delete(entity: SongListEntity)

    @Query("DELETE FROM song_lists")
    fun clear()

    // 关联表操作
    @Insert
    fun addSongToSongList(songlistSong: SongListWithSongEntity)

    @Delete
    fun removeSongFromSongList(songlistSong: SongListWithSongEntity)

    @Transaction
    @Query("SELECT * FROM song_lists WHERE id = :songlistId")
    fun getSongListWithSongs(songlistId: Long): SongListWithSongs?


}