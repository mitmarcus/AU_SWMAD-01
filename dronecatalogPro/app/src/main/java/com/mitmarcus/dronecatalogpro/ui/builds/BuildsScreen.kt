package com.mitmarcus.dronecatalogpro.ui.builds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mitmarcus.dronecatalogpro.data.DroneBuild
import com.mitmarcus.dronecatalogpro.ui.components.BackTopBar
import com.mitmarcus.dronecatalogpro.ui.components.NameDialog

@Composable
fun BuildsScreen(
    onBack: () -> Unit,
    onBuildClick: (DroneBuild) -> Unit,
    viewModel: BuildsViewModel = viewModel()
) {
    val builds by viewModel.builds.collectAsStateWithLifecycle()
    var showNewBuild by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { BackTopBar(title = "My builds", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewBuild = true }) {
                Icon(Icons.Default.Add, contentDescription = "New build")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.error?.let { message ->
                item { Text(message, color = MaterialTheme.colorScheme.error) }
            }
            if (builds.isEmpty()) {
                item { Text("No builds yet. Tap + to start one, or add parts from the catalog.") }
            }
            items(builds, key = { it.id }) { build ->
                Card(onClick = { onBuildClick(build) }, modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(build.name) },
                        supportingContent = { Text("${build.partCount} parts") },
                        leadingContent = { Icon(Icons.Default.Build, contentDescription = null) },
                        trailingContent = {
                            Text(
                                "€${build.totalPrice}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }

    if (showNewBuild) {
        NameDialog(
            title = "New build",
            label = "Build name",
            confirmText = "Create",
            onConfirm = { name ->
                showNewBuild = false
                viewModel.createBuild(name)
            },
            onDismiss = { showNewBuild = false }
        )
    }
}
