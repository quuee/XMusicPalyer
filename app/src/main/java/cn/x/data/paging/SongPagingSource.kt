package cn.x.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.x.data.dao.SongDao
import cn.x.data.db.SongEntity
import kotlinx.coroutines.flow.first

/**
 * 歌曲分页查询, 但是涉及播放队列等不好弄,先放着
 */
class SongPagingSource(
    private val songDao: SongDao,
    private val searchWord: String?,
    private val parentPath: String?,

) : PagingSource<Int, SongEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SongEntity> {
        return try {
            val currentPage = params.key ?: 0 // 默认从第 0 页开始
            val pageSize = params.loadSize

            val songs = songDao.queryLike(
                searchWord = searchWord,
                parentPath = parentPath,
                offset = currentPage * pageSize,
                limit = pageSize
            ).first() // PagingSource 是 suspend 函数，取第一个发射值

            val nextKey = if (songs.isEmpty()) null else currentPage + 1

            LoadResult.Page(
                data = songs,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    // 告诉 Pager 初始加载键是什么
    override fun getRefreshKey(state: PagingState<Int, SongEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1) ?: 0
        }
    }
}