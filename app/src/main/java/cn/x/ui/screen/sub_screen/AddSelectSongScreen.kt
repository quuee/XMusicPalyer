package cn.x.ui.screen.sub_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cn.x.ui.componets.MultiSelectSongItem
import cn.x.util.GlobalMessageUtil
import org.koin.androidx.compose.koinViewModel


/**
 * 添加歌曲到歌单（支持搜索）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSelectSongScreen(
    naviBack: () -> Unit,
    addSelectSongScreenVM: AddSelectSongScreenVM = koinViewModel(),
    songListId: Long
) {

    val items by addSelectSongScreenVM.unselectSongs.collectAsState()
    val selectedIds by addSelectSongScreenVM.selectedIds.collectAsState()
    val searchWord by addSelectSongScreenVM.searchWord.collectAsState()



    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars) // 顶部避开状态栏
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部工具栏
            TopSearchBar(
                naviBack,
                queryWord = searchWord,
                onQueryChange = { addSelectSongScreenVM.changeSearchWord(it) },
                selectDone = { addSelectSongScreenVM.addSelectToSongList(songListId) })
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn() {
                itemsIndexed(items) { index, song ->
                    MultiSelectSongItem(
                        song = song,
                        isSelected = selectedIds.contains(song.uniqueId),
                        isSelectionMode = true,
                        isCurrent = false,
                        onClick = {},
                        onMenuClick = {},
                        onToggleSelection = { addSelectSongScreenVM.toggleSelection(song.uniqueId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopSearchBar(
    naviBack: () -> Unit,
    queryWord: String?,
    onQueryChange: (String?) -> Unit,
    selectDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // 当组件首次显示时自动获取焦点（可选，根据需求决定）
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min), // 确保高度自适应
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 最左侧：返回上级按钮
        IconButton(onClick = naviBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back"
            )
        }

        // 2. 中间：撑满的可输入文本
        TextField(
            value = queryWord ?: "",
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f) // 关键：占据剩余所有空间
                .focusRequester(focusRequester),
            colors = TextFieldDefaults.colors(
                // 移除默认背景，使其看起来像原生 TopBar 的一部分
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = { Text("Search...") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // 已实现根据文字变化实时查询

                    // 关闭键盘
                    focusManager.clearFocus()
                }
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )

        // 3. 最右侧：清除按钮 (仅当有文本时显示)
        if (!queryWord.isNullOrBlank()) {
            IconButton(onClick = {
                onQueryChange("") // 清空文本
                focusRequester.requestFocus() // 清空后重新聚焦（可选）
            }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "clear content",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(onClick = {
            focusManager.clearFocus() // 先 关闭焦点=关闭键盘
            selectDone()
            naviBack()
            GlobalMessageUtil.show("操作成功！", duration = SnackbarDuration.Short)
        }) {
            Icon(imageVector = Icons.Default.Done, contentDescription = null)
        }
    }
}