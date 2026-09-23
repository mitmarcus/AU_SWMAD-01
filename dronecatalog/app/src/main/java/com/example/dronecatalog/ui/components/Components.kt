package com.example.dronecatalog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.dronecatalog.data.PartCategory

// Top bar with a back button, used on the list and detail screens
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
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
