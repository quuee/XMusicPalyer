package cn.x.util


import android.media.MediaMetadataRetriever
import cn.x.data.db.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File

object MusicScanUtil {

    private val AUDIO_EXTENSIONS = setOf(
        "mp3", "flac", "m4a", "aac", "ogg", "opus", "wav", "wma", "ape", "mpc"
    )

    fun scanAudioFiles(directory: File) = callbackFlow {
        if (!directory.exists() || !directory.isDirectory) {
            close()
            return@callbackFlow
        }

        val stack = ArrayDeque<File>()
        stack.add(directory)

        try {
            while (stack.isNotEmpty()) {
                val current = stack.removeFirst()

                // 跳过隐藏目录（如 .trash, .thumbnails）
                if (current.name.startsWith(".")) continue

                val files = withContext(Dispatchers.IO) {
                    current.listFiles()
                }

                files?.forEach { file ->
                    if (file.isDirectory) {
                        stack.add(file)
                    } else if (file.isFile && file.extension.lowercase() in AUDIO_EXTENSIONS) {
                        // 实时发送文件名到 UI
                        trySend(file).isSuccess // 忽略背压失败
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

        awaitClose { /* 取消时清理 */ }
    }.flowOn(Dispatchers.IO)

    suspend fun extractMetadata(file: File): SongEntity? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(file.absolutePath)

//                val songId = retriever.extractMetadata(MediaMetadataRetriever._)
                val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    ?: file.nameWithoutExtension
                val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                    ?: "Unknown Artist"
                val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                    ?: "Unknown Album"
                val durationStr =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val duration = durationStr?.toLongOrNull() ?: 0L

                retriever.release()

                SongEntity(
                    type = SongEntity.LOCAL,
                    songId = 0,
                    title = title,
                    artist = artist,
                    album = album,
                    duration = duration,
                    path = file.absolutePath,
                    parentFolder = file.parent ?: ""
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}