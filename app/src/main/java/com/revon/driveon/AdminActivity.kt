package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import android.util.Log
import com.google.firebase.database.FirebaseDatabase


class AdminActivity : AppCompatActivity() {

    private lateinit var txtFleetUtilization: TextView
    private lateinit var txtFleetInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin)

        txtFleetUtilization = findViewById(R.id.txtFleetUtilization)
        txtFleetInfo = findViewById(R.id.txtFleetInfo)

        loadFleetUtilization()

        // Vehicle Management Card
        findViewById<CardView>(R.id.btnVehicleManagement)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        VehicleAddAndCheckActivity::class.java
                    )
                )
            }

        // Pending KYC Card


        // Registered Users Card
        findViewById<CardView>(R.id.btnRegisteredUsers)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        UsersActivity::class.java
                    )
                )
            }

        // Rejected Users Card


        // Homepage Posts Card
        findViewById<CardView>(R.id.btnPosts)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        PostsActivity::class.java
                    )
                )
            }

        // Bookings Card
        findViewById<CardView>(R.id.btnManageBookings)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        BookingsActivity::class.java
                    )
                )
            }
    }

    private fun loadFleetUtilization() {

        val vehiclesRef = FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("vehicles")

        vehiclesRef.get().addOnSuccessListener { snapshot ->

            val totalVehicles = snapshot.childrenCount.toInt()

            Log.d("FLEET", "Total vehicles = $totalVehicles")

            var availableVehicles = 0

            for (vehicle in snapshot.children) {

                val available =
                    vehicle.child("available").getValue(Boolean::class.java) ?: false

                Log.d(
                    "FLEET",
                    "${vehicle.key} available = $available"
                )

                if (available) {
                    availableVehicles++
                }
            }

            val rentedVehicles = totalVehicles - availableVehicles

            val utilization = if (totalVehicles > 0) {
                (rentedVehicles * 100) / totalVehicles
            } else {
                0
            }

            txtFleetUtilization.text = "$utilization%"
            txtFleetInfo.text = "$rentedVehicles of $totalVehicles bikes currently rented"

            Log.d("FLEET", "Utilization = $utilization%")
        }
    }
}