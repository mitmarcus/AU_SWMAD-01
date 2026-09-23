package com.example.dronecatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dronecatalog.data.Part
import com.example.dronecatalog.data.PartRepository
import com.example.dronecatalog.ui.components.BackTopBar
import com.example.dronecatalog.ui.components.categoryIcon
import com.example.dronecatalog.ui.theme.DronecatalogTheme

class PartDetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PART_ID = "part_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val partId = intent.getIntExtra(EXTRA_PART_ID, -1)
        val part = PartRepository.getPartById(partId)

        setContent {
            DronecatalogTheme {
                PartDetailScreen(part = part, onBack = { finish() })
            }
        }
    }
}

@Composable
fun PartDetailScreen(part: Part?, onBack: () -> Unit) {
    Scaffold(topBar = { BackTopBar(title = part?.name ?: "Part", onBack = onBack) }) { innerPadding ->
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
}

@Composable
fun SpecRow(label: String, value: String) {
    ListItem(
        overlineContent = { Text(label) },
        headlineContent = { Text(value) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Preview
@Composable
fun PartDetailScreenPreview() {
    DronecatalogTheme {
        PartDetailScreen(part = PartRepository.getPartById(4), onBack = {})
    }
}
