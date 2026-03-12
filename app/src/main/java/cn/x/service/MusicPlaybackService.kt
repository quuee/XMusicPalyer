package cn.x.service

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import cn.x.MainActivity
import cn.x.R


/**
 * 音乐播放服务，继承自 [MediaSessionService]。
 * 这是应用的核心后台服务，负责创建和管理 [ExoPlayer] 实例，
 * 并通过 [MediaSession] 与系统媒体中心（如锁屏、通知栏、蓝牙设备）进行交互。
 *
 * 重要职责：
 * 1. 创建并持有 ExoPlayer 实例。
 * 2. 创建并配置 MediaSession，将其与 Player 绑定。
 * 3. 处理来自系统或其他应用的媒体控制命令（play, pause, next等）。
 * 4. 在前台运行以防止被系统杀死。
 */
class MusicPlaybackService : MediaSessionService() {

    private lateinit var exoPlayer: ExoPlayer // 通过 Hilt 注入，确保 Player 在整个应用生命周期内唯一

    private lateinit var mediaSession: MediaSession

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        // 1. 创建 ExoPlayer 实例 (通常通过 Hilt 在 Application 级别提供单例)
        exoPlayer = ExoPlayer.Builder(applicationContext)
            // 自动处理音频焦点
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            // 自动暂停播放
            .setHandleAudioBecomingNoisy(true)
            .build()

        // 2. 创建 PendingIntent 用于启动主 Activity
        val sessionActivityPendingIntent = Intent(this, MainActivity::class.java).let { intent ->
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        // 3. 创建 MediaSession
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(sessionActivityPendingIntent) // 设置点击通知时启动的 Activity
            .build()

        // 4 创建媒体通知
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(applicationContext).build().apply {
                setSmallIcon(R.drawable.music_logo)
            }
        )

    }

    /**
     * 当有客户端（如 UI 的 MediaController）尝试连接时，返回当前的 MediaSession。
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    /**
     * 服务被销毁时，释放资源。
     */
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        mediaSession.release()
    }

//    companion object {
//        val EXTRA_NOTIFICATION = "${CommonApp.app.packageName}.notification"
//    }
}