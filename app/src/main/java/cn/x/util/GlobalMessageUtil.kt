package cn.x.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object GlobalMessageUtil {
    // 全局唯一的 SnackbarHostState
    val snackbarHostState = SnackbarHostState()

    /**
     * 显示一个全局的 Snackbar 消息
     * @param message 消息内容
     * @param actionLabel 操作按钮文本，默认为 null
     * @param duration 显示时长，默认为 Short
     */
    fun show(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        // 使用 Dispatchers.Main 确保在正确的线程上执行
        CoroutineScope(Dispatchers.Main).launch {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
        }
    }

    /**
     * 显示一个 indefinite 时长的 Snackbar，并返回结果
     * 适用于需要处理用户点击操作的场景
     */
    suspend fun showWithResult(
        message: String,
        actionLabel: String,
        duration: SnackbarDuration = SnackbarDuration.Indefinite
    ): SnackbarResult {
        return snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration
        )
    }
}