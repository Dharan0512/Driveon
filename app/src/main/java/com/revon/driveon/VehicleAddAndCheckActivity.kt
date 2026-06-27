package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class VehicleAddAndCheckActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var tvVehicleCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_add_and_check)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)

        tvVehicleCount =
            findViewById(R.id.tvVehicleCount)

        findViewById<ImageView>(R.id.btnBack)
            .setOnClickListener { finish() }

        database =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("vehicles")

        findViewById<android.widget.Button>(R.id.btnAddVehicle)
            .setOnClickListener {

                val vehicleNumber =
                    findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etVehicleNumber)
                        .text.toString().trim().uppercase()

                val bikeName =
                    findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBikeName)
                        .text.toString().trim()

                if (vehicleNumber.isEmpty()) {
                    shake(findViewById(R.id.etVehicleNumber))
                    findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etVehicleNumber)
                        .error = "Required"
                    return@setOnClickListener
                }

                if (bikeName.isEmpty()) {
                    shake(findViewById(R.id.etBikeName))
                    findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBikeName)
                        .error = "Required"
                    return@setOnClickListener
                }

                val fields = listOf(

                    findViewById<android.widget.EditText>(R.id.etDay1Price),
                    findViewById<android.widget.EditText>(R.id.etDay1Km),
                    findViewById<android.widget.EditText>(R.id.etDay1Extra),

                    findViewById<android.widget.EditText>(R.id.etDay2Price),
                    findViewById<android.widget.EditText>(R.id.etDay2Km),
                    findViewById<android.widget.EditText>(R.id.etDay2Extra),

                    findViewById<android.widget.EditText>(R.id.etDay7Price),
                    findViewById<android.widget.EditText>(R.id.etDay7Km),
                    findViewById<android.widget.EditText>(R.id.etDay7Extra),

                    findViewById<android.widget.EditText>(R.id.etDay30Price),
                    findViewById<android.widget.EditText>(R.id.etDay30Km),
                    findViewById<android.widget.EditText>(R.id.etDay30Extra)
                )

                for (field in fields) {

                    if (field.text.toString().trim().isEmpty()) {

                        field.error = "Required"

                        Toast.makeText(
                            this,
                            "Fill all package fields",
                            Toast.LENGTH_SHORT
                        ).show()

                        field.requestFocus()

                        return@setOnClickListener
                    }
                }

                val vehicle = Vehicle(
                    vehicleNumber = vehicleNumber,
                    bikeName = bikeName,
                    day1Price = findViewById<android.widget.EditText>(R.id.etDay1Price).text.toString(),
                    day1KmLimit = findViewById<android.widget.EditText>(R.id.etDay1Km).text.toString(),
                    day1ExtraKm = findViewById<android.widget.EditText>(R.id.etDay1Extra).text.toString(),

                    day2Price = findViewById<android.widget.EditText>(R.id.etDay2Price).text.toString(),
                    day2KmLimit = findViewById<android.widget.EditText>(R.id.etDay2Km).text.toString(),
                    day2ExtraKm = findViewById<android.widget.EditText>(R.id.etDay2Extra).text.toString(),

                    day7Price = findViewById<android.widget.EditText>(R.id.etDay7Price).text.toString(),
                    day7KmLimit = findViewById<android.widget.EditText>(R.id.etDay7Km).text.toString(),
                    day7ExtraKm = findViewById<android.widget.EditText>(R.id.etDay7Extra).text.toString(),

                    day30Price = findViewById<android.widget.EditText>(R.id.etDay30Price).text.toString(),
                    day30KmLimit = findViewById<android.widget.EditText>(R.id.etDay30Km).text.toString(),
                    day30ExtraKm = findViewById<android.widget.EditText>(R.id.etDay30Extra).text.toString()
                )

                FirebaseDatabase.getInstance()
                    .getReference("Driveon")
                    .child("vehicles")
                    .child(vehicleNumber)
                    .setValue(vehicle)
                    .addOnSuccessListener {

                        Toast.makeText(this, "Vehicle Saved", Toast.LENGTH_SHORT).show()

                        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                        // show success animation (optional image)
                        val successView = findViewById<ImageView>(R.id.imgSuccess)
                        successView.visibility = View.VISIBLE

                        val anim = AnimationUtils.loadAnimation(this, R.anim.success_pop)
                        successView.startAnimation(anim)

                        // auto hide after delay
                        successView.postDelayed({
                            successView.visibility = View.GONE
                        }, 1200)

                        // CLEAR FIELDS
                        clearAllFields()

                        // AUTO SCROLL TO TOP
                        scrollView.post {
                            scrollView.smoothScrollTo(0, 0)
                        }
                    }
                    .addOnFailureListener {
                        android.widget.Toast.makeText(this, "Failed: ${it.message}", android.widget.Toast.LENGTH_LONG).show()
                    }
            }

        findViewById<Button>(R.id.btnManageVehicles)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        ManageVehiclesActivity::class.java
                    )
                )
            }

        loadVehicleCount()
    }

    private fun loadVehicleCount() {

        database.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    tvVehicleCount.text =
                        snapshot.childrenCount.toString()
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )
    }

    private fun shake(view: android.view.View) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.shake)
        view.startAnimation(anim)
    }

    private fun clearAllFields() {

        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBikeName).text?.clear()
        findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etVehicleNumber).text?.clear()

        findViewById<android.widget.EditText>(R.id.etDay1Price).text.clear()
        findViewById<android.widget.EditText>(R.id.etDay1Km).text.clear()
        findViewById<android.widget.EditText>(R.id.etDay1Extra).text.clear()

        findViewById<android.widget.EditText>(R.id.etDay2Price).text.clear()
        findViewById<android.widget.EditText>(R.id.etDay2Km).text.clear()
        findViewById<android.widget.EditText>(R.id.etDay2Extra).text.clear()

        findViewById<android.widget.EditText>(R.id.etDay7Price).text.clear()
        findViewById<android.widget.EditText>(R.id.etDay7Km).text.clear()
        findViewById<android.widget.EditText>(R.id.etDay7Extra).text.clear()

        findViewById<android.widget.EditText>(R.id.etDay30Price).text.clear()
        findViewById<android.widget.EditText>(R.id.etDay30Km).text.clear()
        findViewById<android.widget.EditText>(R.id.etDay30Extra).text.clear()
    }
}