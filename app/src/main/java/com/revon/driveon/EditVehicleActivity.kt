package com.revon.driveon

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class EditVehicleActivity : AppCompatActivity() {

    private lateinit var database:
            com.google.firebase.database.DatabaseReference

    private lateinit var vehicleNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_vehicle)

        database =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("vehicles")

        vehicleNumber =
            intent.getStringExtra("vehicleNumber")
                ?: ""

        val etVehicleNumber =
            findViewById<EditText>(R.id.etVehicleNumber)

        etVehicleNumber.setText(vehicleNumber)

        val bikeName =
            findViewById<EditText>(R.id.etBikeName)

        val day1 =
            findViewById<EditText>(R.id.etDay1Price)

        val day2 =
            findViewById<EditText>(R.id.etDay2Price)

        val day7 =
            findViewById<EditText>(R.id.etDay7Price)

        val day30 =
            findViewById<EditText>(R.id.etDay30Price)

        val day1ExtraKm =
            findViewById<EditText>(R.id.etDay1ExtraKm)

        val day2ExtraKm =
            findViewById<EditText>(R.id.etDay2ExtraKm)

        val day7ExtraKm =
            findViewById<EditText>(R.id.etDay7ExtraKm)

        val day30ExtraKm =
            findViewById<EditText>(R.id.etDay30ExtraKm)

        bikeName.setText(
            intent.getStringExtra("bikeName")
        )

        day1.setText(
            intent.getStringExtra("day1Price")
        )

        day2.setText(
            intent.getStringExtra("day2Price")
        )

        day7.setText(
            intent.getStringExtra("day7Price")
        )

        day30.setText(
            intent.getStringExtra("day30Price")
        )

        day1ExtraKm.setText(
            intent.getStringExtra("day1ExtraKm")
        )

        day2ExtraKm.setText(
            intent.getStringExtra("day2ExtraKm")
        )

        day7ExtraKm.setText(
            intent.getStringExtra("day7ExtraKm")
        )

        day30ExtraKm.setText(
            intent.getStringExtra("day30ExtraKm")
        )

        findViewById<Button>(R.id.btnUpdate)
            .setOnClickListener {

                val updates = HashMap<String, Any>()

                updates["bikeName"] =
                    bikeName.text.toString().trim()

                updates["day1Price"] =
                    day1.text.toString().trim()

                updates["day2Price"] =
                    day2.text.toString().trim()

                updates["day7Price"] =
                    day7.text.toString().trim()

                updates["day30Price"] =
                    day30.text.toString().trim()

                updates["day1ExtraKm"] =
                    day1ExtraKm.text.toString().trim()

                updates["day2ExtraKm"] =
                    day2ExtraKm.text.toString().trim()

                updates["day7ExtraKm"] =
                    day7ExtraKm.text.toString().trim()

                updates["day30ExtraKm"] =
                    day30ExtraKm.text.toString().trim()

                database
                    .child(vehicleNumber)
                    .updateChildren(updates)

                Toast.makeText(
                    this,
                    "Vehicle Updated Successfully",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
    }
}