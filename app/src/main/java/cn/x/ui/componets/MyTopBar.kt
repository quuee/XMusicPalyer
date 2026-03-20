package cn.x.ui.componets


import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterTopBar(
    title: String?,
    drawerToggle: () -> Unit,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title ?: "",
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = drawerToggle) {
                Icon(Icons.Filled.Menu, contentDescription = "Drawer Menu")
            }
        },
        actions = actions
    )
}
