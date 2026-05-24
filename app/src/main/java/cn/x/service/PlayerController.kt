package cn.x.service

import androidx.annotation.MainThread
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import kotlinx.coroutines.flow.StateFlow

interface PlayerController {
    val mediaController: MediaController
    val playlist: StateFlow<List<MediaItem>>
    val currentSong: StateFlow<MediaItem?>
    val playState: StateFlow<PlayState>
    val playProgress: StateFlow<Long>
    val bufferingPercent: StateFlow<Int>
    val playMode: StateFlow<PlayMode>

    fun addAndPlay(song: MediaItem)

    fun replaceAll(songList: List<MediaItem>, song: MediaItem)

    fun play(mediaId: String)

    fun delete(song: MediaItem)

    fun clearPlaylist()

    fun playPause()

    fun next()

    fun prev()

    fun seekTo(msec: Long)

    fun getAudioSessionId(): Int

    fun setPlayMode(mode: PlayMode)

    fun stop()
}