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

@Dao
interface SongListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSongList(songList: SongListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSongListAll(list: List<SongListEntity>)

    @Query("SELECT * FROM song_lists order by sort")
    fun getAllSongLists(): List<SongListEntity>

    @Update
    fun updateSongList(entity: SongListEntity)

    @Update
    fun updateSongListAll(list: List<SongListEntity>)

    @Delete
    fun delete(entity: SongListEntity)

    @Query("DELETE FROM song_lists")
    fun clear()

    // 关联表操作
    @Insert
    fun insertSongsToSongList(songs: List<SongListWithSongEntity>)

    @Delete
    fun deleteSongFromSongList(songlistSong: SongListWithSongEntity)

    @Transaction
    @Query("SELECT * FROM song_lists WHERE id = :songlistId")
    fun getSongsBySongListId(songlistId: Long): SongListWithSongs?

    @Query("DELETE FROM songlist_song WHERE songlist_id = :songlistId")
    fun deleteAllBySongListId(songlistId: Long)

}