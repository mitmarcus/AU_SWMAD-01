package com.mitmarcus.dronecatalogpro.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mitmarcus.dronecatalogpro.data.DroneBuild
import com.mitmarcus.dronecatalogpro.data.Part
import com.mitmarcus.dronecatalogpro.ui.builds.BuildsViewModel
import com.mitmarcus.dronecatalogpro.ui.components.BackTopBar
import com.mitmarcus.dronecatalogpro.ui.components.NameDialog
import com.mitmarcus.dronecatalogpro.ui.components.categoryIcon
import kotlinx.coroutines.launch

@Composable
fun PartDetailScreen(
    part: Part?,
    onBack: () -> Unit,
    onOpenBuild: (String) -> Unit,
    buildsViewModel: BuildsViewModel = viewModel()
) {
    val builds by buildsViewModel.builds.collectAsStateWithLifecycle()
    var showPicker by remember { mutableStateOf(false) }
    var showNewBuild by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(buildsViewModel.error) {
        buildsViewModel.error?.let { snackbar.showSnackbar(it) }
    }

    fun added(buildId: String, buildName: String) {
        scope.launch {
            val result = snackbar.showSnackbar("Added to $buildName", actionLabel = "Open")
            if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) onOpenBuild(buildId)
        }
    }

    Scaffold(
        topBar = { BackTopBar(title = part?.name ?: "Part", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { innerPadding ->
        if (part == null) {
            Text("Part not found", modifier = Modifier.padding(innerPadding).padding(16.dp))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        categoryIcon(part.category),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(part.name, style = MaterialTheme.typography.headlineMedium)
                    Text("by ${part.brand}", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        part.category.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "€${part.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Button(onClick = { showPicker = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Add to build", modifier = Modifier.padding(start = 8.dp))
            }

            Text("Description", style = MaterialTheme.typography.titleMedium)
            Text(part.description, style = MaterialTheme.typography.bodyLarge)

            Text("Specifications", style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                part.specs.entries.forEachIndexed { index, spec ->
                    if (index > 0) HorizontalDivider()
                    SpecRow(label = spec.key, value = spec.value)
                }
            }
        }
    }

    if (part != null && showPicker) {
        BuildPickerDialog(
            builds = builds,
            onPick = { build ->
                showPicker = false
                buildsViewModel.addPart(build.id, part.id)
                added(build.id, build.name)
            },
            onNewBuild = {
                showPicker = false
                showNewBuild = true
            },
            onDismiss = { showPicker = false }
        )
    }

    if (part != null && showNewBuild) {
        NameDialog(
            title = "New build",
            label = "Build name",
            confirmText = "Create and add",
            onConfirm = { name ->
                showNewBuild = false
                buildsViewModel.createBuildWithPart(name, part.id) { buildId -> added(buildId, name) }
            },
            onDismiss = { showNewBuild = false }
        )
    }
}

@Composable
private fun BuildPickerDialog(
    builds: List<DroneBuild>,
    onPick: (DroneBuild) -> Unit,
    onNewBuild: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to which build?") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (builds.isEmpty()) Text("You have no builds yet.")
                builds.forEach { build ->
                    ListItem(
                        headlineContent = { Text(build.name) },
                        supportingContent = { Text("${build.partCount} parts, €${build.totalPrice}") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        trailingContent = {
                            TextButton(onClick = { onPick(build) }) { Text("Add") }
                        }
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onNewBuild) { Text("New build") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SpecRow(label: String, value: String) {
    ListItem(
        overlineContent = { Text(label) },
        headlineContent = { Text(value) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
