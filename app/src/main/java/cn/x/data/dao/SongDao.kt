package cn.x.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.x.data.db.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: SongEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<SongEntity>)

    @Query("SELECT * FROM songs")
    fun queryAll(): List<SongEntity>

    @Query(
        """
        SELECT * FROM songs 
    WHERE (:searchWord IS NULL 
           OR INSTR(title, :searchWord) > 0 
           OR INSTR(artist, :searchWord) > 0 
           OR INSTR(album, :searchWord) > 0)
    ORDER BY title ASC
    """
    )
    fun queryLike(searchWord: String?): List<SongEntity>
    @Query(
        """
        SELECT * FROM songs 
        WHERE (:searchWord IS NULL OR title LIKE :searchWord)
          AND (:searchWord IS NULL OR artist LIKE :searchWord)
          AND (:searchWord IS NULL OR album LIKE :searchWord)
          AND (:parentPath IS NULL OR relative_path = :parentPath)
        ORDER BY title ASC
        LIMIT :limit OFFSET :offset
    """
    )
    fun queryLike(searchWord: String?,parentPath: String?,offset: Int, limit: Int): Flow<List<SongEntity>>

    @Query("""
        SELECT COUNT(*) FROM songs
         WHERE (:searchWord IS NULL OR title LIKE :searchWord)
          AND (:searchWord IS NULL OR artist LIKE :searchWord)
          AND (:searchWord IS NULL OR album LIKE :searchWord)
          AND (:parentPath IS NULL OR relative_path = :parentPath)
    """)
    fun queryLikeCount(searchWord: String?,parentPath: String?): Int

    @Delete
    fun delete(entity: SongEntity)

    @Query("DELETE FROM songs")
    fun clear()
}