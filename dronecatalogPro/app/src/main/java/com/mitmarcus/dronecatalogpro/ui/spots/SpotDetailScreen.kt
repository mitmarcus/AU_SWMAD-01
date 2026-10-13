package com.mitmarcus.dronecatalogpro.ui.spots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.mitmarcus.dronecatalogpro.data.FlyingSpot
import com.mitmarcus.dronecatalogpro.data.weather.FlyVerdict
import com.mitmarcus.dronecatalogpro.data.weather.FlyingConditions
import com.mitmarcus.dronecatalogpro.data.weather.HourForecast
import com.mitmarcus.dronecatalogpro.data.weather.weatherDescription
import com.mitmarcus.dronecatalogpro.ui.components.BackTopBar
import com.mitmarcus.dronecatalogpro.ui.components.Loadable

@Composable
fun SpotDetailScreen(onBack: () -> Unit, viewModel: SpotDetailViewModel = viewModel()) {
    val state by viewModel.spot.collectAsStateWithLifecycle()
    val spot = (state as? Loadable.Ready)?.value
    var confirmDelete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            BackTopBar(title = spot?.name ?: "Flying spot", onBack = onBack) {
                if (spot != null) {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh forecast")
                    }
                    IconButton(onClick = { confirmDelete = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete spot")
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val current = state) {
                Loadable.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                Loadable.Missing -> Text("Spot not found", modifier = Modifier.padding(16.dp))
                is Loadable.Ready -> SpotContent(current.value, viewModel.weather, viewModel::refresh)
            }
        }
    }

    if (confirmDelete && spot != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete ${spot.name}?") },
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
private fun SpotContent(spot: FlyingSpot, weather: WeatherState, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "%.5f, %.5f".format(spot.latitude, spot.longitude),
            style = MaterialTheme.typography.bodyMedium
        )

        when (weather) {
            WeatherState.Loading -> CircularProgressIndicator()
            is WeatherState.Error -> {
                Text(weather.message, color = MaterialTheme.colorScheme.error)
                Button(onClick = onRetry) { Text("Try again") }
            }
            is WeatherState.Ready -> Conditions(weather.conditions)
        }

        Text(
            "Weather data by Open-Meteo.com",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun Conditions(conditions: FlyingConditions) {
    val now = conditions.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = verdictColor(conditions.verdict))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(conditions.verdict.title, style = MaterialTheme.typography.headlineSmall, color = Color.Black)
            Text(conditions.verdict.advice, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Now (${now.time.substringAfter('T')})", style = MaterialTheme.typography.titleMedium)
            Text(weatherDescription(now.weatherCode))
            Text("Temperature: %.1f °C".format(now.temperature))
            Text("Wind: %.1f m/s, gusts %.1f m/s".format(now.windSpeed, now.windGusts))
            Text("Precipitation: %.1f mm".format(now.precipitation))
        }
    }

    conditions.bestHour?.let { best ->
        Text(
            "Calmest hour in the next 12: ${best.time} (gusts %.1f m/s, %d%% rain)".format(
                best.windGusts, best.rainChance
            ),
            style = MaterialTheme.typography.titleSmall
        )
    }

    Text("Next 12 hours", style = MaterialTheme.typography.titleMedium)
    Card(modifier = Modifier.fillMaxWidth()) {
        conditions.hours.forEachIndexed { index, hour ->
            if (index > 0) HorizontalDivider()
            HourRow(hour)
        }
    }
}

@Composable
private fun HourRow(hour: HourForecast) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(hour.time, style = MaterialTheme.typography.titleSmall)
        Text("%.1f / %.1f m/s".format(hour.windSpeed, hour.windGusts))
        Text("${hour.rainChance}% rain")
    }
}

private fun verdictColor(verdict: FlyVerdict) = when (verdict) {
    FlyVerdict.GOOD -> Color(0xFF81C784)
    FlyVerdict.MARGINAL -> Color(0xFFFFD54F)
    FlyVerdict.NO_FLY -> Color(0xFFE57373)
}
