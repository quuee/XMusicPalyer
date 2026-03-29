package cn.x.ui.componets

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomSnackbar(snackbarData: SnackbarData) {

    val colorScheme = MaterialTheme.colorScheme

    Snackbar(
        snackbarData = snackbarData,
        modifier = Modifier.padding(16.dp),
        actionOnNewLine = false,
        shape = SnackbarDefaults.shape,
        containerColor = colorScheme.surface.copy(alpha = 0.8f),
        contentColor = colorScheme.onSurface,
        actionColor = colorScheme.secondaryContainer,
        actionContentColor = colorScheme.onSecondaryContainer,
        dismissActionContentColor = colorScheme.onSecondaryContainer
    )

}