package com.example.dronecatalog

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dronecatalog.data.PartCategory
import com.example.dronecatalog.data.PartRepository
import com.example.dronecatalog.ui.components.categoryIcon
import com.example.dronecatalog.ui.theme.DronecatalogTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categories = PartRepository.getCategories()

        setContent {
            DronecatalogTheme {
                MainScreen(
                    categories = categories,
                    onCategoryClick = { category ->
                        val intent = Intent(this, PartListActivity::class.java)
                        intent.putExtra(PartListActivity.EXTRA_CATEGORY, category.name)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(categories: List<PartCategory>, onCategoryClick: (PartCategory) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "FPV Parts",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                categories.forEach { category ->
                    NavigationDrawerItem(
                        icon = { Icon(categoryIcon(category), contentDescription = null) },
                        label = { Text(category.title) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onCategoryClick(category)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("FPV Parts") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Handyman,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(96.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text("FPV Parts from memory", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(24.dp))
                Button(onClick = { scope.launch { drawerState.open() } }) {
                    Text("Open menu")
                }
            }
        }
    }
}

// Oooo hello, you're actually looking at the code
@Preview
@Composable
fun MainScreenPreview() {
    DronecatalogTheme {
        MainScreen(categories = PartRepository.getCategories(), onCategoryClick = {})
    }
}
