package com.mitmarcus.dronecatalogpro.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.mitmarcus.dronecatalogpro.data.PartCategory

// Top bar with a back button, used on every screen below the home screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(title: String, onBack: () -> Unit, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = actions
    )
}

// Dialog with one text field, used to name builds and flying spots
@Composable
fun NameDialog(
    title: String,
    label: String,
    confirmText: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(label) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name.trim()) }, enabled = name.isNotBlank()) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun categoryIcon(category: PartCategory): ImageVector {
    return when (category) {
        PartCategory.FRAME -> Icons.Default.CropFree
        PartCategory.MOTOR -> Icons.Default.Autorenew
        PartCategory.ESC -> Icons.Default.FlashOn
        PartCategory.FLIGHT_CONTROLLER -> Icons.Default.Memory
        PartCategory.CAMERA -> Icons.Default.Videocam
        PartCategory.VTX -> Icons.Default.Cast
        PartCategory.ANTENNA -> Icons.Default.SettingsInputAntenna
    }
}
