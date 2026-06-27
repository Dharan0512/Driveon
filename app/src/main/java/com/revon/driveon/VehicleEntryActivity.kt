package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class VehicleEntryActivity : AppCompatActivity() {

    private lateinit var etVehicleNumber: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_entry)

        etVehicleNumber = findViewById(R.id.etVehicleNumber)
        val btnContinue = findViewById<TextView>(R.id.btnContinue)

        btnContinue.setOnClickListener {

            val vehicleNo =
                etVehicleNumber.text.toString()
                    .trim()
                    .uppercase()

            if (vehicleNo.isEmpty()) {
                etVehicleNumber.error = "Enter Vehicle Number"
                return@setOnClickListener
            }

            verifyVehicle(vehicleNo)
        }
    }

    private fun verifyVehicle(vehicleNumber: String) {

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("vehicles")
            .child(vehicleNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        checkKycStatus(vehicleNumber)
                    } else {

                        Toast.makeText(
                            this@VehicleEntryActivity,
                            "Vehicle Not Available In Collection",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun checkKycStatus(vehicleNumber: String) {

        val prefs =
            getSharedPreferences(
                "DriveOnPrefs",
                MODE_PRIVATE
            )

        val phone =
            prefs.getString("phone", "") ?: ""

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("userprofiles")
            .child(phone)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val status =
                        snapshot.child("kycStatus")
                            .getValue(String::class.java)
                            ?: ""

                    when (status) {

                        "approved" -> {

                            val intent =
                                Intent(
                                    this@VehicleEntryActivity,
                                    BookingDetailsActivity::class.java
                                )

                            intent.putExtra(
                                "vehicleNumber",
                                vehicleNumber
                            )

                            startActivity(intent)
                        }

                        "pending" -> {

                            Toast.makeText(
                                this@VehicleEntryActivity,
                                "KYC Verification Pending",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        "rejected" -> {

                            val reason =
                                snapshot.child("rejectionReason")
                                    .getValue(String::class.java)
                                    ?: "Verification Failed"

                            Toast.makeText(
                                this@VehicleEntryActivity,
                                reason,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {

                            Toast.makeText(
                                this@VehicleEntryActivity,
                                "Complete Profile Verification First",
                                Toast.LENGTH_LONG
                            ).show()

                            startActivity(
                                Intent(
                                    this@VehicleEntryActivity,
                                    ProfileActivity::class.java
                                )
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}
