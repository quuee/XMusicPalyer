package cn.x.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 辅助函数：格式化时间
fun formatTime(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / 1000) / 60
    return "%02d:%02d".format(minutes, seconds)
}

fun getCurrentDateTime(): String {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return now.format(formatter)
}