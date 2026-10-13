package com.mitmarcus.dronecatalogpro.ui.spots

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mitmarcus.dronecatalogpro.data.FlyingSpot
import com.mitmarcus.dronecatalogpro.data.LocationHelper
import com.mitmarcus.dronecatalogpro.ui.components.BackTopBar
import com.mitmarcus.dronecatalogpro.ui.components.NameDialog
import kotlinx.coroutines.launch

// How much of the spot list shows before it is dragged up
private val SHEET_PEEK_HEIGHT = 170.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotsMapScreen(
    onBack: () -> Unit,
    onSpotClick: (FlyingSpot) -> Unit,
    viewModel: SpotsViewModel = viewModel()
) {
    val context = LocalContext.current
    val spots by viewModel.spots.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var locating by remember { mutableStateOf(false) }
    var myLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var pendingSpot by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let { snackbar.showSnackbar(it) }
    }

    // Gets a GPS fix, moves the map there and asks for a name for the new spot
    fun locateAndAdd() {
        scope.launch {
            locating = true
            val location = LocationHelper.currentLocation(context)
            locating = false
            if (location == null) {
                snackbar.showSnackbar("No GPS fix. Turn on location and try again.")
            } else {
                myLocation = location.latitude to location.longitude
                pendingSpot = myLocation
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        if (granted.values.any { it }) locateAndAdd()
        else scope.launch { snackbar.showSnackbar("Location permission is needed to save a spot") }
    }

    // Focus on the user's position after a GPS fix, otherwise on the newest spot
    val focus = myLocation ?: spots.firstOrNull()?.let { it.latitude to it.longitude }

    // The spot list is a bottom sheet over the map, so any number of spots fits:
    // it peeks at the bottom and can be dragged up to show the whole list
    BottomSheetScaffold(
        topBar = { BackTopBar(title = "Flying spots", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
        sheetPeekHeight = SHEET_PEEK_HEIGHT,
        sheetContent = { SpotList(spots = spots, onSpotClick = onSpotClick) }
    ) { innerPadding ->
        // Bottom padding keeps the map and the button above the peeking sheet
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(bottom = SHEET_PEEK_HEIGHT)
                .fillMaxSize()
        ) {
            OsmMap(
                spots = spots,
                focus = focus,
                onSpotClick = onSpotClick,
                modifier = Modifier.fillMaxSize()
            )
            ExtendedFloatingActionButton(
                onClick = {
                    if (LocationHelper.hasPermission(context)) locateAndAdd()
                    else permissionLauncher.launch(LocationHelper.permissions)
                },
                icon = {
                    if (locating) CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                    else Icon(Icons.Default.MyLocation, contentDescription = null)
                },
                text = { Text("Save my location") },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            )
        }
    }

    pendingSpot?.let { (lat, lon) ->
        NameDialog(
            title = "Save flying spot",
            label = "Spot name",
            confirmText = "Save",
            onConfirm = { name ->
                pendingSpot = null
                viewModel.addSpot(name, lat, lon)
            },
            onDismiss = { pendingSpot = null }
        )
    }
}

@Composable
private fun SpotList(spots: List<FlyingSpot>, onSpotClick: (FlyingSpot) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            Text(
                if (spots.size == 1) "1 flying spot" else "${spots.size} flying spots",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        if (spots.isEmpty()) {
            item {
                Text(
                    "No spots yet. Go to a field and tap \"Save my location\".",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        items(spots, key = { it.id }) { spot ->
            ListItem(
                headlineContent = { Text(spot.name) },
                supportingContent = { Text("%.5f, %.5f".format(spot.latitude, spot.longitude)) },
                leadingContent = { Icon(Icons.Default.Place, contentDescription = null) },
                // Same color as the sheet instead of the default surface color
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                modifier = Modifier.fillMaxWidth().clickable { onSpotClick(spot) }
            )
        }
    }
}
