package cn.x.service

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import cn.x.data.dao.PlayListDao
import cn.x.util.Constants
import cn.x.util.SPUtil
import cn.x.util.toMediaItem
import cn.x.util.toPlayListSongEntity
import cn.x.util.toSongEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.collections.indexOfFirst
import kotlin.collections.map

class PlayerControllerImpl
    (
    private val player: MediaController,
    private val playListDao: PlayListDao,
    private val applicationScope: CoroutineScope,
) : PlayerController {

    override val mediaController: MediaController
        get() = player

    override val playlist: StateFlow<List<MediaItem>> =
        playListDao.queryAll().map { entities ->
            entities.map { it.toSongEntity().toMediaItem() }
        }
            .stateIn(
                scope = applicationScope,  // 使用应用级作用域
//                started = SharingStarted.WhileSubscribed(1000),  // 延迟3秒停止订阅
                started = SharingStarted.Lazily, // 无延迟
                initialValue = emptyList()
            )

    private val _currentSong = MutableStateFlow<MediaItem?>(null)
    override val currentSong = _currentSong.asStateFlow()

    private val _playState = MutableStateFlow<PlayState>(PlayState.Idle)
    override val playState = _playState.asStateFlow()

    private val _playProgress = MutableStateFlow<Long>(0)
    override val playProgress = _playProgress.asStateFlow()

    private val _bufferingPercent = MutableStateFlow(0)
    override val bufferingPercent = _bufferingPercent.asStateFlow()

    private val _playMode = MutableStateFlow(PlayMode.valueOf(SPUtil.getInt(Constants.PlayMode)))
    override val playMode: StateFlow<PlayMode> = _playMode.asStateFlow()

    private var audioSessionId = 0

    init {
        player.playWhenReady = false
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        _playState.value = PlayState.Idle
                        _playProgress.value = 0
                        _bufferingPercent.value = 0
                    }

                    Player.STATE_BUFFERING -> {
                        _playState.value = PlayState.Preparing
                    }

                    Player.STATE_READY -> {
                        player.play()
                        _playState.value = PlayState.Playing
                    }

                    Player.STATE_ENDED -> {}
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (player.playbackState == Player.STATE_READY) {
                    _playState.value = if (isPlaying) PlayState.Playing else PlayState.Pause
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                mediaItem ?: return
                val playlist = playlist.value
                _currentSong.value = playlist.find { it.mediaId == mediaItem.mediaId }
            }

            @OptIn(UnstableApi::class)
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                super.onAudioSessionIdChanged(audioSessionId)
                this@PlayerControllerImpl.audioSessionId = audioSessionId
            }


            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                stop()
                //toast("播放失败(${error.errorCodeName},${error.localizedMessage})")
            }
        })

        setPlayMode(PlayMode.valueOf(SPUtil.getInt(Constants.PlayMode)))

        player.setMediaItems(playlist.value)

        val currentSongId = SPUtil.getString(Constants.CurrentSongId)
        if (currentSongId.isNotEmpty()) {
            val currentSongIndex =
                playlist.value.indexOfFirst { it.mediaId == currentSongId }.coerceAtLeast(0)
            _currentSong.value = playlist.value[currentSongIndex]
            player.seekTo(currentSongIndex, 0)
        }

        applicationScope.launch {
            while (isActive) {
                if (player.isPlaying) {
                    _playProgress.value = player.currentPosition
                }
                delay(1000)
            }
        }

    }


    override fun addAndPlay(song: MediaItem) {
        applicationScope.launch {
            val newPlaylist = playlist.value.toMutableList()
            val index = playlist.value.indexOfFirst { it.mediaId == song.mediaId }
            if (index >= 0) {
                newPlaylist[index] = song
                player.replaceMediaItem(index, song)
            } else {
                newPlaylist.add(song)
                player.addMediaItem(song)
            }
            playListDao
                .replaceAll(newPlaylist.map { it.toSongEntity().toPlayListSongEntity() })
            play(song.mediaId)
        }
    }


    override fun replaceAll(songList: List<MediaItem>, song: MediaItem) {
        applicationScope.launch {
            playListDao.replaceAll(songList.map { it.toSongEntity().toPlayListSongEntity() })
            stop()
            player.setMediaItems(songList)
            play(song.mediaId)
        }
    }

    override fun play(mediaId: String) {
        // 在添加歌曲,更新数据库后playlist.value不是最新.导致找不到歌曲
        applicationScope.launch {
            var playlist = playlist.value
            var index = playlist.indexOfFirst { it.mediaId == mediaId }

            if (index < 0) {

                playlist = playListDao.queryAll().first()
                    .map { it.toSongEntity().toMediaItem() }
                index = playlist.indexOfFirst { it.mediaId == mediaId }

            }
            if (index < 0 || playlist.isEmpty()) return@launch

            stop()
            player.seekTo(index, 0)
            player.prepare()

            _currentSong.value = playlist[index]
            _playProgress.value = 0
            _bufferingPercent.value = 0
        }

    }

    override fun delete(song: MediaItem) {
        applicationScope.launch {
            val playlist = playlist.value.toMutableList()
            val index = playlist.indexOfFirst { it.mediaId == song.mediaId }
            if (index < 0) return@launch
            if (playlist.size == 1) {
                clearPlaylist()
            } else {
                playlist.removeAt(index)
                playListDao.delete(song.toSongEntity().toPlayListSongEntity())
                player.removeMediaItem(index)
            }
        }
    }

    override fun clearPlaylist() {
        applicationScope.launch {
            playListDao.clear()
            player.clearMediaItems()
            _currentSong.value = null
        }
    }

    override fun playPause() {
        if (player.mediaItemCount == 0) return
        when (player.playbackState) {
            Player.STATE_IDLE -> {
                player.prepare()
            }

            Player.STATE_BUFFERING -> {
                stop()
            }

            Player.STATE_READY -> {
                if (player.isPlaying) {
                    player.pause()
                    _playState.value = PlayState.Pause
                } else {
                    player.play()
                    _playState.value = PlayState.Playing
                }
            }

            Player.STATE_ENDED -> {
                player.seekToNextMediaItem()
                player.prepare()
            }
        }
    }

    override fun next() {
        if (player.mediaItemCount == 0) return
        player.seekToNextMediaItem()
        player.prepare()
        _playProgress.value = 0
        _bufferingPercent.value = 0
    }

    override fun prev() {
        if (player.mediaItemCount == 0) return
        player.seekToPreviousMediaItem()
        player.prepare()
        _playProgress.value = 0
        _bufferingPercent.value = 0
    }

    override fun seekTo(msec: Long) {
        if (player.playbackState == Player.STATE_READY) {
            player.seekTo(msec)
        }
    }

    override fun getAudioSessionId(): Int {
        return audioSessionId
    }

    override fun setPlayMode(mode: PlayMode) {
        SPUtil.putInt(Constants.PlayMode, mode.value)
        _playMode.value = mode
        when (mode) {
            PlayMode.Loop -> {
                player.repeatMode = Player.REPEAT_MODE_ALL
                player.shuffleModeEnabled = false
            }

            PlayMode.Shuffle -> {
                player.repeatMode = Player.REPEAT_MODE_ALL
                player.shuffleModeEnabled = true
            }

            PlayMode.Single -> {
                player.repeatMode = Player.REPEAT_MODE_ONE
                player.shuffleModeEnabled = false
            }
        }
    }

    override fun stop() {
        player.stop()
        _playState.value = PlayState.Idle
    }
}