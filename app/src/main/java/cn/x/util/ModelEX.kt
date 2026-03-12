package cn.x.util


import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import cn.x.data.db.PlayListSongEntity
import cn.x.data.db.SongEntity


const val PARAM_ID = "id"
const val EXTRA_DURATION = "duration"
const val EXTRA_FILE_PATH = "file_path"
const val EXTRA_FILE_NAME = "file_name"
const val EXTRA_FILE_SIZE = "file_size"
const val EXTRA_BASE_COVER = "base_cover"


fun SongEntity.toMediaItem(): MediaItem {
    if (
        type == SongEntity.LOCAL
    ) {
        return MediaItem.Builder()
            .setMediaId(uniqueId)
            .setUri(contentUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setAlbumArtist(artist)
                    //.setArtworkUri(getLargeCover().toUri())
//                .setBaseCover(albumCover)
                    .setDuration(duration)
                    .setFilePath(path)
                    .setFileName(fileName)
                    .setFileSize(fileSize)
                    .build()
            )
            .build()
    } else {
        return MediaItem.Builder()
            .setMediaId(uniqueId)
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setAlbumArtist(artist)
                    //.setArtworkUri(getLargeCover().toUri())
//                .setBaseCover(albumCover)
                    .setDuration(duration)
                    .setFilePath(path)
                    .setFileName(fileName)
                    .setFileSize(fileSize)
                    .build()
            )
            .build()
    }

}

fun MediaItem.toSongEntity(): SongEntity {
    return SongEntity(
        type = getSongType(),
        songId = getSongId(),
        title = mediaMetadata.title?.toString() ?: "",
        artist = mediaMetadata.artist?.toString() ?: "",
        artistId = 0,
        album = mediaMetadata.albumTitle?.toString() ?: "",
        albumId = 0,
//        albumCover = mediaMetadata.getBaseCover() ?: "",
        duration = mediaMetadata.getDuration(),
        uri = localConfiguration?.uri?.toString() ?: "",
        path = mediaMetadata.getFilePath(),
        fileName = mediaMetadata.getFileName(),
        fileSize = mediaMetadata.getFileSize()
    )
}


fun generateUniqueId(type: Int, songId: Long): String {
    return "$type#$songId"
}

fun MediaItem.isLocal(): Boolean {
    return getSongType() == SongEntity.LOCAL
}

fun MediaItem.getSongType(): Int {
    return mediaId.split("#").firstOrNull()?.toIntOrNull() ?: SongEntity.LOCAL
}

fun MediaItem.getSongId(): Long {
    return mediaId.split("#").getOrNull(1)?.toLongOrNull() ?: 0L
}

fun MediaMetadata.Builder.setDuration(duration: Long) = apply {
    val extras = build().extras ?: bundleOf()
    extras.putLong(EXTRA_DURATION, duration)
    setExtras(extras)
}

fun MediaMetadata.getDuration(): Long {
    return extras?.getLong(EXTRA_DURATION) ?: 0
}

fun MediaMetadata.Builder.setFilePath(value: String) = apply {
    val extras = build().extras ?: bundleOf()
    extras.putString(EXTRA_FILE_PATH, value)
    setExtras(extras)
}

fun MediaMetadata.getFilePath(): String {
    return extras?.getString(EXTRA_FILE_PATH) ?: ""
}

fun MediaMetadata.Builder.setFileName(name: String) = apply {
    val extras = build().extras ?: bundleOf()
    extras.putString(EXTRA_FILE_NAME, name)
    setExtras(extras)
}

fun MediaMetadata.getFileName(): String {
    return extras?.getString(EXTRA_FILE_NAME) ?: ""
}

fun MediaMetadata.Builder.setFileSize(size: Long) = apply {
    val extras = build().extras ?: bundleOf()
    extras.putLong(EXTRA_FILE_SIZE, size)
    setExtras(extras)
}

fun MediaMetadata.getFileSize(): Long {
    return extras?.getLong(EXTRA_FILE_SIZE) ?: 0
}

fun MediaMetadata.Builder.setBaseCover(value: String) = apply {
    val extras = build().extras ?: bundleOf()
    extras.putString(EXTRA_BASE_COVER, value)
    setExtras(extras)
}

fun MediaMetadata.getBaseCover(): String? {
    return extras?.getString(EXTRA_BASE_COVER)
}

fun PlayListSongEntity.toSongEntity(): SongEntity {
    return SongEntity(
        type,
        songId,
        title,
        artist,
        artistId,
        album,
        albumId,
        duration,
        uri,
        path,
        fileName,
        fileSize,
        contentUri,
        parentFolder,
        uniqueId
    )
}

fun List<PlayListSongEntity>.toSongEntityList(): List<SongEntity> = map { it.toSongEntity() }

fun SongEntity.toPlayListSongEntity(): PlayListSongEntity {
    return PlayListSongEntity(
        type,
        songId,
        title,
        artist,
        artistId,
        album,
        albumId,
        duration,
        uri,
        path,
        fileName,
        fileSize,
        contentUri,
        parentFolder,
        uniqueId
    )
}
