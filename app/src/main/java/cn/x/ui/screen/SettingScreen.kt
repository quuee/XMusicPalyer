package cn.x.ui.screen

import android.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.x.ui.componets.CenterTopBar

@Composable
fun SettingScreen(onDrawerToggle: () -> Unit) {

    var expanded by remember { mutableStateOf(false) }

    var selected by remember { mutableStateOf(0) }
    val items = listOf("跟随系统", "白天", "黑夜")

    Scaffold(
        topBar = {
            CenterTopBar(
                title = "Setting",
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

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // 通用设置
                // 主题、均衡器
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

                ) {
                    Column() {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ModernSelectionCards(
                                options = items,
                                selectedIndex = selected,
                                onOptionSelected = { selected = it }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {},
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("均衡器")
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = ""
                            )
                        }
                    }
                }

                // 本地
                // 扫描选项，是否扫描小于30秒的音频
                Text(
                    "本地",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

                ) {
                    Column() {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("是否扫描小于30秒的音频")
                            Switch(
                                checked = false,
                                onCheckedChange = {}
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("是否显示桌面歌词")
                            Switch(
                                checked = false,
                                onCheckedChange = {}
                            )
                        }

                    }
                }

                // 网络选项
                Text(
                    "网络选项",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                // 是否使用蜂窝网络、边听边存、缓存上限
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

                ) {
                    Column() {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("使用蜂窝网络")
                            Switch(
                                checked = false,
                                onCheckedChange = {}
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("边听边存")
                            Switch(
                                checked = false,
                                onCheckedChange = {}
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("缓存上限")
                            Box {
                                OutlinedButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier.width(100.dp)
//                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                                ) {
                                    Text(String.format("%dGB", 4))
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Select protocol"
                                    )
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("2GB") },
                                        onClick = {
//                                    cacheMax = 2
                                            expanded = false

                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("4GB") },
                                        onClick = {
//                                    cacheMax = 4
                                            expanded = false

                                        }
                                    )
                                }
                            }
                        }

                    }
                }

            }
        }
    }
}

@Composable
private fun ModernSelectionCards(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    // 定义颜色
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.secondaryContainer
    val borderColor = if (selectedIndex != -1) selectedColor else unselectedColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            // 可选：作为可选择组，提升无障碍支持
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex

            Card(
                modifier = Modifier
                    .weight(1f)
                    // 【关键】使用 Modifier.border 代替过时的 card border 参数
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 2.dp,
                                color = selectedColor,
                                shape = CardDefaults.shape
                            )
                        } else {
                            Modifier.border(
                                width = 1.dp,
                                color = unselectedColor,
                                shape = CardDefaults.shape
                            )
                        }
                    ),
                // 【关键】使用 CardDefaults.cardColors 设置背景色
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) selectedColor.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                ),
                // 【关键】使用 CardDefaults.cardElevation 设置阴影
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 4.dp else 1.dp
                ),
                onClick = { onOptionSelected(index) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        // 【关键】动态字体粗细
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}