package com.mitmarcus.dronecatalogpro.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

object LocationHelper {

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun hasPermission(context: Context): Boolean = permissions.any {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // Asks the fused location provider for a fresh GPS fix. Returns null when location is off.
    @SuppressLint("MissingPermission")
    suspend fun currentLocation(context: Context): Location? {
        if (!hasPermission(context)) return null
        val client = LocationServices.getFusedLocationProviderClient(context)
        val cancellation = CancellationTokenSource()
        return try {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellation.token).await()
        } finally {
            cancellation.cancel()
        }
    }
}
