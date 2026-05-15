package com.example.namma_hasiru.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationHelper(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationReceived: (Location?) -> Unit) {
        val cancellationTokenSource = CancellationTokenSource()
        
        // Try getting last known location first (much faster)
        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
            if (lastLoc != null) {
                Log.d("LocationHelper", "Using last known location")
                onLocationReceived(lastLoc)
            } else {
                // If last known is null, request fresh location
                Log.d("LocationHelper", "Requesting fresh location...")
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location: Location? ->
                    Log.d("LocationHelper", "Fresh location received: ${location?.latitude}")
                    onLocationReceived(location)
                }.addOnFailureListener { e ->
                    Log.e("LocationHelper", "Location request failed", e)
                    onLocationReceived(null)
                }
            }
        }
    }
}
