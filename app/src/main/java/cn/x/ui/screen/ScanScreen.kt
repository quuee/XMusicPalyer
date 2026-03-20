package cn.x.ui.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cn.x.data.db.SongEntity
import cn.x.ui.componets.CenterTopBar
import cn.x.ui.componets.FolderCard
import kotlinx.coroutines.flow.StateFlow
import java.io.File

/**
 * 扫描歌曲
 */
@Composable
fun ScanScreen(
    scanVM: ScanScreenVM = hiltViewModel(),
    onDrawerToggle: () -> Unit
) {

    val context = LocalContext.current

    val colorScheme = MaterialTheme.colorScheme

    val scanState by scanVM.scanState.collectAsState()
    val currentFolders by scanVM.currentFolders.collectAsState()

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // 申请权限启动器
    val permissionLauncher = rememberLauncherForActivityResult( //(compose专用)
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scanVM.startScanByMediaStore()
        } else {
            Toast.makeText(context, "需要存储权限才能扫描音乐", Toast.LENGTH_SHORT).show()
        }
    }

    // 检查权限状态
    val hasPermission = remember {
        ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    // 选择目录 添加自定义文件夹
    val (selectedDirUri, pickDirectory) = rememberDirectoryPicker(context)

    Scaffold(
        topBar = {
            CenterTopBar(
                title = "Scan",
                onDrawerToggle,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    if (hasPermission) {
                        scanVM.startScanByMediaStore()
                    } else {
                        permissionLauncher.launch(permission)

                    }

                }) {
                Text("开始扫描", color = colorScheme.onSurface)
            }
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    pickDirectory()
                }
            ) {
                Text("添加自定义文件夹", color = colorScheme.onSurface)
            }

            // 扫描 添加后的文件夹
            LazyColumn {
                itemsIndexed(currentFolders) { index: Int, folder ->
                    FolderCard(
                        folder.folderPath,
                        folder.songCount,
                        onClick = {}
                    )
                }
            }

//                selectedDirUri?.let { uri ->
//                    Text(
//                        text = "已选择目录: ${uri.path}",
//                        modifier = Modifier.padding(top = 16.dp)
//                    )
//                }

        }

        ScanningCard(scanState, { scanVM.onDismiss() }, scanVM.musicList)
    }


}

@Composable
private fun ScanningCard(
    scanState: ScanState,
    onDismiss: () -> Unit,
    musicListState: StateFlow<List<SongEntity>>
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val musicList by musicListState.collectAsState()
    // 创建一个滚动状态
    val listState = rememberLazyListState()

    // 关键：监听 musicList (收集到的值) 本身的变化
    //  当 StateFlow 发出新值，musicList 引用改变，LaunchedEffect 触发
    LaunchedEffect(musicList) { // 注意：key 是 musicList 列表对象本身
        // 使用 snapshotFlow 监听布局完成，比 delay 更可靠
        snapshotFlow { listState.layoutInfo.totalItemsCount }
            .collect { totalItems ->
                // 当布局的项目总数等于我们期望的列表大小时，执行滚动
                if (totalItems == musicList.size && musicList.isNotEmpty()) {
                    listState.animateScrollToItem(musicList.size - 1)
                    // 注意：collect 会持续监听。如果只想在 *新增* 时滚动
                }
            }
    }

    if (scanState != ScanState.Idle) {
        Dialog(
            onDismissRequest = onDismiss,
        ) {
            Card(
                modifier = Modifier
                    .height(screenHeight * 0.7f)
                    .width(screenWidth * 0.7f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("正在扫描本地音乐")
                        if (scanState == ScanState.Scanning) {
                            CircularProgressIndicator()
                        } else {

                        }

                    }

                    // 歌曲列表
                    LazyColumn(
                        modifier = Modifier.weight(0.75f),
                        state = listState,
//                        reverseLayout = false // 设置为 false，从顶部开始显示
                    ) {
                        itemsIndexed(musicList) { index, item ->
                            Text(item.title)
                        }
                    }

                    // 按钮（完成后可点击关闭dialog）
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onDismiss,
                        enabled = scanState != ScanState.Scanning
                    ) {
                        Text("完成")
                    }
                }
            }
        }
    }

}

@Composable
private fun rememberDirectoryPicker(context: Context): Pair<Uri?, () -> Unit> {

    val selectedDirUri = remember { mutableStateOf<Uri?>(null) }

    val directoryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri ->
            uri?.let {
                // 获取持久化权限
                val contentResolver = context.contentResolver
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)

                selectedDirUri.value = uri
            }
        }
    )

    return Pair(selectedDirUri.value) {
        directoryPickerLauncher.launch(null)
    }
}