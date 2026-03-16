package cn.x.util

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import cn.x.data.db.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import kotlinx.coroutines.flow.flow

// 查询字段
private val LocalAudioColumns = arrayOf(
    MediaStore.Audio.AudioColumns._ID, // 音频id
    MediaStore.Audio.AudioColumns.RELATIVE_PATH, // 音频相对路径
//    MediaStore.Audio.AudioColumns.DATA, // 文件绝对路径 老api
    MediaStore.Audio.AudioColumns.SIZE, // 音频字节大小
    MediaStore.Audio.AudioColumns.DISPLAY_NAME, // 音频名称 xxx.amr
    MediaStore.Audio.AudioColumns.TITLE, // 音频标题
    MediaStore.Audio.AudioColumns.DATE_ADDED, // 音频添加到MediaProvider的时间
    MediaStore.Audio.AudioColumns.DATE_MODIFIED, // 上次修改时间，该列用于内部MediaScanner扫描，外部不要修改
    MediaStore.Audio.AudioColumns.MIME_TYPE, // 音频类型
    MediaStore.Audio.AudioColumns.DURATION, // 音频时长
    MediaStore.Audio.AudioColumns.BOOKMARK, // 上次音频的回放位置
    MediaStore.Audio.AudioColumns.ARTIST_ID, // 艺人id
    MediaStore.Audio.AudioColumns.ARTIST, // 艺人名称
    MediaStore.Audio.AudioColumns.ALBUM_ID, // 艺人专辑id
    MediaStore.Audio.AudioColumns.ALBUM, // 艺人专辑名称
    MediaStore.Audio.AudioColumns.TRACK, MediaStore.Audio.AudioColumns.YEAR, // 录制音频的年份
    MediaStore.Audio.AudioColumns.IS_MUSIC, // 是否为音乐音频
    MediaStore.Audio.AudioColumns.IS_PODCAST, MediaStore.Audio.AudioColumns.IS_RINGTONE, // 是否为警告音频
    MediaStore.Audio.AudioColumns.IS_ALARM, // 是否为闹钟音频
    MediaStore.Audio.AudioColumns.IS_NOTIFICATION, // 是否为通知音频,

)

/**
 * android mediaStore不会主动将新的音频文件加入音乐库，只能扫描出部分歌曲(试过在文件夹单独点击几首没扫描出的歌曲播放，再去扫描才出现)
 */
class MusicScanUtilByMediaStoreFlow(private val context: Context) {

    /**
     * 扫描设备上所有音乐文件并以流形式返回
     * @param minDuration 最小持续时间(毫秒)，默认60秒
     */
    fun scanAllMusicAsFlow(minDuration: Long = 30_000): Flow<SongEntity> {
        return scanMusicInternalAsFlow(null, minDuration)
    }

    private fun scanMusicInternalAsFlow(folderUri: Uri?, minDuration: Long): Flow<SongEntity> =
        callbackFlow {
            withContext(Dispatchers.IO) {
                val resolver: ContentResolver = context.contentResolver
                val selection = buildSelection(folderUri)
                val selectionArgs = buildSelectionArgs(folderUri, minDuration)
                val sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
                val uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

                val cursor: Cursor? = resolver.query(
                    uri,
                    LocalAudioColumns,
                    selection,
                    selectionArgs,
                    sortOrder
                )
                cursor?.use {
                    Log.d("DEBUG", "Total audio files in MediaStore: ${it.count}")
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val durationColumn =
                        it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                    val relativePathColumn =
                        it.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)
                    val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val albumIdColumn =
                        it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                    while (it.moveToNext()) {
                        val id = it.getLong(idColumn)
                        val title = it.getString(titleColumn)
                        val artist = it.getString(artistColumn)
                        val duration = it.getLong(durationColumn)
                        val size = it.getLong(sizeColumn)
                        val relativePath = it.getString(relativePathColumn)
                        val album = it.getString(albumColumn)
                        val albumId = it.getLong(albumIdColumn)

                        val artworkUri = ContentUris.withAppendedId(
                            "content://media/external/audio/albumart".toUri(),
                            albumId
                        )

                        // 可用于本地播放的uri
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val songItem = SongEntity(
                            type = SongEntity.LOCAL,
                            songId = id,
                            title = title,
                            artist = artist,
                            duration = duration,
                            fileSize = size,
                            path = relativePath,
                            album = album,
                            albumId = albumId,
                            artworkUri = artworkUri.toString(),
                            uri = contentUri.toString(),
                            )

                        send(songItem) // 发送每首歌曲到流
                        // 因为callbackFlow有缓冲区，最大64，导致歌曲扫描后发送失败，数量一直不对
                    }
                }
                close()
            }
        }

    private fun buildSelection(folderUri: Uri?): String {
        val selection = StringBuilder()
//        selection.append("(${MediaStore.Audio.Media.IS_MUSIC} != 0 OR ${MediaStore.Audio.Media.IS_MUSIC} IS NULL)")
//        selection.append(" AND ${MediaStore.Audio.Media.DURATION}>=? ")
        selection.append(" ${MediaStore.Audio.Media.DURATION} >= ? ")
        folderUri?.let {
            it.path ?: return@let
            selection.append(" AND ${MediaStore.Audio.Media.RELATIVE_PATH} LIKE ?")
        }
        return selection.toString()
    }

    private fun buildSelectionArgs(folderUri: Uri?, minDuration: Long): Array<String> {
        return buildList {
            add(minDuration.toString())
            folderUri?.path?.let { path ->
                add("${path}%")
            }
        }.toTypedArray()
    }

}