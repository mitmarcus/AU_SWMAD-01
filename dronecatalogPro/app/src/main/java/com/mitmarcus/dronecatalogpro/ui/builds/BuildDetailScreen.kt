package com.mitmarcus.dronecatalogpro.ui.builds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mitmarcus.dronecatalogpro.data.DroneBuild
import com.mitmarcus.dronecatalogpro.data.PartRepository
import com.mitmarcus.dronecatalogpro.ui.components.BackTopBar
import com.mitmarcus.dronecatalogpro.ui.components.Loadable
import com.mitmarcus.dronecatalogpro.ui.components.categoryIcon

@Composable
fun BuildDetailScreen(
    onBack: () -> Unit,
    onPartClick: (Int) -> Unit,
    viewModel: BuildDetailViewModel = viewModel()
) {
    val state by viewModel.build.collectAsStateWithLifecycle()
    var confirmDelete by remember { mutableStateOf(false) }
    val build = (state as? Loadable.Ready)?.value

    Scaffold(
        topBar = {
            BackTopBar(title = build?.name ?: "Build", onBack = onBack) {
                if (build != null) {
                    IconButton(onClick = { confirmDelete = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete build")
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val current = state) {
                Loadable.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                Loadable.Missing -> Text(
                    viewModel.error ?: "Build not found",
                    modifier = Modifier.padding(16.dp)
                )
                is Loadable.Ready -> BuildParts(
                    build = current.value,
                    error = viewModel.error,
                    onPartClick = onPartClick,
                    onAdd = viewModel::addOne,
                    onRemove = viewModel::removeOne
                )
            }
        }
    }

    if (confirmDelete && build != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete ${build.name}?") },
            text = { Text("The build and its part list are removed from your account.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    viewModel.delete(onDeleted = onBack)
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun BuildParts(
    build: DroneBuild,
    error: String?,
    onPartClick: (Int) -> Unit,
    onAdd: (Int) -> Unit,
    onRemove: (Int, Int) -> Unit
) {
    // Show the parts in catalog order: frame first, antenna last
    val rows = build.parts.entries
        .mapNotNull { (partId, quantity) -> PartRepository.getPartById(partId)?.let { it to quantity } }
        .sortedBy { (part, _) -> part.category.ordinal }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Total €${build.totalPrice}", style = MaterialTheme.typography.headlineSmall)
            Text("${build.partCount} parts", style = MaterialTheme.typography.bodyMedium)
        }
        error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        if (rows.isEmpty()) {
            item { Text("No parts yet. Open a part in the catalog and tap \"Add to build\".") }
        }
        items(rows, key = { (part, _) -> part.id }) { (part, quantity) ->
            Card(onClick = { onPartClick(part.id) }, modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    overlineContent = { Text(part.category.title) },
                    headlineContent = { Text("${part.brand} ${part.name}") },
                    supportingContent = { Text("€${part.price} each, €${part.price * quantity} total") },
                    leadingContent = { Icon(categoryIcon(part.category), contentDescription = null) },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onRemove(part.id, quantity) }) {
                                Icon(Icons.Default.Remove, contentDescription = "Remove one")
                            }
                            Text("$quantity", style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { onAdd(part.id) }) {
                                Icon(Icons.Default.Add, contentDescription = "Add one")
                            }
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}
