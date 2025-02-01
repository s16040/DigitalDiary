package com.example.digitaldiary.utils

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.util.Locale

class LocationUtils(private val context: Context) {
    private val geocoder = Geocoder(context, Locale.getDefault())
    
    suspend fun getCurrentLocation(fusedLocationClient: FusedLocationProviderClient): Pair<Location?, String?> {
        try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()

            location?.let {
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                val cityName = addresses?.firstOrNull()?.let { address ->
                    address.locality ?: address.subAdminArea ?: address.adminArea
                }
                return Pair(location, cityName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Pair(null, null)
    }
}