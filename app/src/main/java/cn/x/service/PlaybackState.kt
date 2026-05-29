package cn.x.service

import androidx.media3.common.MediaItem
import cn.x.util.Constants
import cn.x.util.SPUtil

data class PlaybackState(
    val playlist: List<MediaItem> = emptyList(),
    val currentTrack: MediaItem? = null,
//    val lyrics: Lyrics? = null,
//    val isLoadingLyrics: Boolean = false,
    val playState: PlayState = PlayState.Idle,
    val playbackMode: PlayMode = PlayMode.valueOf(SPUtil.getInt(Constants.PlayMode)),
    val playProgress: Long = 0L,
    val bufferingPercent: Long = 0L,
)