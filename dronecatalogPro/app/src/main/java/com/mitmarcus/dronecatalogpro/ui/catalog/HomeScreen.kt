package com.mitmarcus.dronecatalogpro.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mitmarcus.dronecatalogpro.data.PartCategory
import com.mitmarcus.dronecatalogpro.data.PartRepository
import com.mitmarcus.dronecatalogpro.ui.components.categoryIcon
import com.mitmarcus.dronecatalogpro.ui.theme.DronecatalogTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    categories: List<PartCategory>,
    onCategoryClick: (PartCategory) -> Unit,
    onBuildsClick: () -> Unit,
    onSpotsClick: () -> Unit,
    onSignOut: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Closes the drawer, then runs the click
    fun drawerClick(action: () -> Unit): () -> Unit = {
        scope.launch { drawerState.close() }
        action()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "FPV Parts Pro",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                )
                Text(
                    userName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp)
                )
                HorizontalDivider()
                DrawerItem("My builds", Icons.Default.Build, drawerClick(onBuildsClick))
                DrawerItem("Flying spots", Icons.Default.Map, drawerClick(onSpotsClick))
                HorizontalDivider()
                categories.forEach { category ->
                    DrawerItem(category.title, categoryIcon(category), drawerClick { onCategoryClick(category) })
                }
                HorizontalDivider()
                DrawerItem("Sign out", Icons.AutoMirrored.Filled.Logout, drawerClick(onSignOut))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("FPV Parts Pro") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("Catalog", style = MaterialTheme.typography.titleMedium)
                }
                items(categories) { category ->
                    Card(onClick = { onCategoryClick(category) }, modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            headlineContent = { Text(category.title) },
                            supportingContent = {
                                Text("${PartRepository.getPartsByCategory(category).size} parts")
                            },
                            leadingContent = { Icon(categoryIcon(category), contentDescription = null) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

// Oooo hello, you're actually looking at the code
@Preview
@Composable
fun HomeScreenPreview() {
    DronecatalogTheme {
        HomeScreen(
            userName = "pilot@example.com",
            categories = PartRepository.getCategories(),
            onCategoryClick = {},
            onBuildsClick = {},
            onSpotsClick = {},
            onSignOut = {}
        )
    }
}
