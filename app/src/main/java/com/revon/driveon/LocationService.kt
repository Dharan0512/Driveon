package com.revon.driveon

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.firebase.database.FirebaseDatabase

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()

        Log.d("REVON_LOCATION", "SERVICE STARTED")

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Location request (every 10 seconds)
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).build()

        // Callback for location updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {

                Log.d("REVON_LOCATION", "CALLBACK TRIGGERED")
                val location = result.lastLocation ?: return

                if (location == null) {
                    Log.d("REVON_LOCATION", "Location is NULL")
                    return
                }

                val lat = location.latitude
                val lng = location.longitude

                Log.d("REVON_LOCATION", "LAT=$lat LNG=$lng")

                val ref = FirebaseDatabase.getInstance()
                    .getReference("live_locations")

                val data = HashMap<String, Any>()
                data["lat"] = lat
                data["lng"] = lng
                data["time"] = System.currentTimeMillis()

                // TODO: replace "user1" with phone/userId later
              //  ref.child("user1").setValue(data)
                ref.child("user1").push().setValue(data)
            }
        }

        // Start location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("REVON_LOCATION", "NO PERMISSION")
            return
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}