package com.mitmarcus.dronecatalogpro.ui.spots

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mitmarcus.dronecatalogpro.data.FlyingSpot
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File

// Aarhus, used as the map center before any spot or GPS fix exists
private val DEFAULT_CENTER = GeoPoint(56.1629, 10.2039)

// OpenStreetMap view (osmdroid) wrapped for Compose. One marker per flying spot.
@Composable
fun OsmMap(
    spots: List<FlyingSpot>,
    focus: Pair<Double, Double>?,
    onSpotClick: (FlyingSpot) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnSpotClick by rememberUpdatedState(onSpotClick)

    val mapView = remember {
        // The OSM tile servers require a user agent. Tiles are cached in the app's cache dir.
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidBasePath = File(context.cacheDir, "osmdroid")
            osmdroidTileCache = File(osmdroidBasePath, "tiles")
        }
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            // Stop the map from repeating up and down or scrolling past the poles
            isVerticalMapRepetitionEnabled = false
            setScrollableAreaLimitLatitude(MapView.getTileSystem().maxLatitude, MapView.getTileSystem().minLatitude, 0)
            minZoomLevel = 3.0
            controller.setZoom(11.0)
            controller.setCenter(DEFAULT_CENTER)
        }
    }

    // MapView needs the activity's resume/pause events
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    LaunchedEffect(focus) {
        focus?.let { (lat, lon) -> mapView.controller.animateTo(GeoPoint(lat, lon), 15.0, 800L) }
    }

    AndroidView(
        factory = { mapView },
        // osmdroid draws whole tiles and markers even past its edges, so clip them
        // or they cover the top bar and the spot list while dragging
        modifier = modifier.clipToBounds(),
        update = { map ->
            map.overlays.removeAll { it is Marker }
            spots.forEach { spot ->
                val marker = Marker(map).apply {
                    position = GeoPoint(spot.latitude, spot.longitude)
                    title = spot.name
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ ->
                        currentOnSpotClick(spot)
                        true
                    }
                }
                map.overlays.add(marker)
            }
            map.invalidate()
        }
    )
}
