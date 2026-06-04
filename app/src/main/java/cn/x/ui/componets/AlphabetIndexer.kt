package cn.x.ui.componets


import android.view.HapticFeedbackConstants
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import cn.x.util.ConfigKeys
import kotlin.math.max
import kotlin.math.min

/**
 * 侧边字母索引组件
 * @param onLetterSelected 字母选中回调
 * 优化触摸反馈
 */
@Composable
fun AlphabetIndexSidebar(
    onLetterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLetter by remember { mutableStateOf<String?>(null) }
    var showPopup by remember { mutableStateOf(false) }
    // 获取 View 用于触发震动反馈
    val view = LocalView.current

    Column(
        modifier = modifier
            .width(30.dp)
            .fillMaxHeight()
            .padding(end = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly // 均匀分布
    ) {
        ConfigKeys.alphabet.forEach { letter ->
            val isSelected = selectedLetter == letter
            Text(
                text = letter,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .weight(1f) // 让每个字母占据均等高度以便触摸
                    .wrapContentSize()
            )
        }
    }
    // 全局触摸检测覆盖层
    // 这样子触摸反馈好很多,不会即使点这里但是等于没点到
    Box(
        modifier = modifier
            .width(40.dp) // 稍微宽一点方便手指滑动
            .fillMaxHeight()
            .pointerInput(Unit) {
                awaitEachGesture {
                    while (true) {
                        val event = awaitPointerEvent()
                        val changed = event.changes.firstOrNull()
                        if (changed != null && changed.pressed) {
                            // 获取触摸位置对应的字母
                            val letter = getLetterAtPosition(
                                changed.position.y,
                                size.height,
                                ConfigKeys.alphabet.size
                            )

                            // 只有当字母改变时才触发
                            if (letter != selectedLetter) {
                                selectedLetter = letter
                                showPopup = true

                                // 触发震动反馈(需要设置声音和震动里打开)
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

                                // 通知外部
                                onLetterSelected(letter)
                            }

                            changed.consume()
                        } else {
                            // 手指抬起时隐藏弹窗
                            if (event.changes.all { !it.pressed }) {
                                showPopup = false
                                selectedLetter = null
                            }
                            break
                        }
                    }
                }
            }
    )
    // 中间放大字母弹窗
    if (showPopup && selectedLetter != null) {
        Popup(
            alignment = Alignment.CenterEnd,
            onDismissRequest = { showPopup = false }
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedLetter!!,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .wrapContentSize()
                )
            }
        }
    }
}


/**
 * 根据 Y 轴位置计算对应的字母
 */
private fun getLetterAtPosition(y: Float, height: Int, totalLetters: Int): String {
    if (height <= 0) return ConfigKeys.alphabet[0]

    val index = ((y / height) * totalLetters).toInt()
    return ConfigKeys.alphabet[max(0, min(index, totalLetters - 1))]
}

