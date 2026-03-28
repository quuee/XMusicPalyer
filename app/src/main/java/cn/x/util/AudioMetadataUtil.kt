package cn.x.util


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.images.Artwork
import java.io.File

object AudioMetadataUtil {

    /**
     * 读取音频文件的元数据
     */
    fun readMetadata(filePath: String): Map<String, String?> {
        val file = File(filePath)
        if (!file.exists()) return emptyMap()

        return try {
            val audioFile: AudioFile = AudioFileIO.read(file)
            val tag: Tag? = audioFile.tagOrCreateDefault
            mapOf(
                "title" to tag?.getFirst(FieldKey.TITLE),
                "artist" to tag?.getFirst(FieldKey.ARTIST),
                "album" to tag?.getFirst(FieldKey.ALBUM),
                "track" to tag?.getFirst(FieldKey.TRACK),
                "lyrics" to tag?.getFirst(FieldKey.LYRICS),
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    /**
     * 获取音频封面（Bitmap），若无则返回 null
     */
    fun getAlbumArt(filePath: String): Bitmap? {
        val file = File(filePath)
        if (!file.exists()) return null

        return try {
            val audioFile: AudioFile = AudioFileIO.read(file)
            val tag = audioFile.tag
            val artwork: Artwork? = tag?.firstArtwork
            if (artwork != null) {
                val binaryData = artwork.binaryData
                BitmapFactory.decodeByteArray(binaryData, 0, binaryData.size)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 更新音频元数据（支持 title, artist, album 等）
     */
    fun updateMetadata(
        filePath: String,
        title: String? = null,
        artist: String? = null,
        album: String? = null,
        year: String? = null,
        track: String? = null,
        genre: String? = null
    ): Boolean {
        val file = File(filePath)
        if (!file.exists()) return false

        return try {
            val audioFile: AudioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateDefault

            title?.let { tag.setField(FieldKey.TITLE, it) }
            artist?.let { tag.setField(FieldKey.ARTIST, it) }
            album?.let { tag.setField(FieldKey.ALBUM, it) }
            year?.let { tag.setField(FieldKey.YEAR, it) }
            track?.let { tag.setField(FieldKey.TRACK, it) }
            genre?.let { tag.setField(FieldKey.GENRE, it) }

            AudioFileIO.write(audioFile)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 设置专辑封面（从 Bitmap 写入）
     */
//    fun setAlbumArt(filePath: String, bitmap: Bitmap): Boolean {
//        val file = File(filePath)
//        if (!file.exists()) return false
//
//        return try {
//            // 先将 Bitmap 转为 JPEG 字节数组
//            val stream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
//            val imageData = stream.toByteArray()
//            stream.close()
//
//            val audioFile: AudioFile = AudioFileIO.read(file)
//            val tag = audioFile.tagOrCreateDefault
//
//            // 清除旧封面（可选）
//            tag.deleteArtworkField()
//
//            // 添加新封面
//            tag.setField(StandardArtwork.createArtworkFromFile())
//
//            AudioFileIO.write(audioFile)
//            true
//        } catch (e: IOException) {
//            e.printStackTrace()
//            false
//        }
//    }

//    fun bitmapToFile(bitmap: Bitmap, context: Context): File? {
//        return try {
//            val tempFile = File.createTempFile("album_art", ".jpg", context.cacheDir)
//            val fos = FileOutputStream(tempFile)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
//            fos.flush()
//            fos.close()
//            tempFile
//        } catch (e: IOException) {
//            e.printStackTrace()
//            null
//        }
//    }
}