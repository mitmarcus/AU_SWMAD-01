package com.example.dronecatalog

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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
import com.example.dronecatalog.data.PartCategory
import com.example.dronecatalog.data.PartRepository
import com.example.dronecatalog.ui.components.BackTopBar
import com.example.dronecatalog.ui.components.categoryIcon
import com.example.dronecatalog.ui.theme.DronecatalogTheme

class PartListActivity : ComponentActivity() {

    companion object {
        const val EXTRA_CATEGORY = "category"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryName = intent.getStringExtra(EXTRA_CATEGORY) ?: PartCategory.FRAME.name
        val category = PartCategory.valueOf(categoryName)
        val parts = PartRepository.getPartsByCategory(category)

        setContent {
            DronecatalogTheme {
                PartListScreen(
                    title = category.title,
                    parts = parts,
                    onBack = { finish() },
                    onPartClick = { part ->
                        val intent = Intent(this, PartDetailActivity::class.java)
                        intent.putExtra(PartDetailActivity.EXTRA_PART_ID, part.id)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun PartListScreen(
    title: String,
    parts: List<Part>,
    onBack: () -> Unit,
    onPartClick: (Part) -> Unit
) {
    Scaffold(topBar = { BackTopBar(title = title, onBack = onBack) }) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(parts) { part ->
                PartListItem(part = part, onClick = { onPartClick(part) })
            }
        }
    }
}

@Composable
fun PartListItem(part: Part, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        ListItem(
            overlineContent = { Text(part.brand) },
            headlineContent = { Text(part.name) },
            supportingContent = { Text(part.keySpec) },
            leadingContent = { Icon(categoryIcon(part.category), contentDescription = null) },
            trailingContent = {
                Text(
                    "€${part.price}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Preview
@Composable
fun PartListScreenPreview() {
    DronecatalogTheme {
        PartListScreen(
            title = PartCategory.MOTOR.title,
            parts = PartRepository.getPartsByCategory(PartCategory.MOTOR),
            onBack = {},
            onPartClick = {}
        )
    }
}
