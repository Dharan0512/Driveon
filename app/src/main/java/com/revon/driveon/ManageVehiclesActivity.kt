package com.revon.driveon

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.widget.TextView
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputEditText
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ManageVehiclesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference

    private lateinit var etSearch: TextInputEditText

    private val allVehicles = ArrayList<Vehicle>()

    private lateinit var txtVehicleCount: TextView

    private lateinit var layoutEmpty: LinearLayout



    private val vehicleList =
        ArrayList<Vehicle>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_vehicles)

        txtVehicleCount = findViewById(R.id.txtVehicleCount)

        etSearch = findViewById(R.id.etSearch)

        layoutEmpty = findViewById(R.id.layoutEmpty)

        etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                filterVehicles(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        recyclerView =
            findViewById(R.id.recyclerVehicles)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        database =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("vehicles")

        loadVehicles()
    }

    private fun loadVehicles() {

        database.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    allVehicles.clear()
                    vehicleList.clear()



                    for (vehicleSnap in snapshot.children) {

                        try {

                            val vehicle =
                                vehicleSnap.getValue(
                                    Vehicle::class.java
                                )

                            if (vehicle != null) {
                                allVehicles.add(vehicle)
                                vehicleList.add(vehicle)
                            }

                        } catch (e: Exception) {

                            // Skip old boolean records
                        }
                    }

                    txtVehicleCount.text =
                        "Total Vehicle Count : ${vehicleList.size}"

                        recyclerView.adapter =
                        VehicleAdapter(
                            vehicleList
                        ) { vehicle ->

                            showDeleteDialog(vehicle)
                        }

                    if (vehicleList.isEmpty()) {

                        layoutEmpty.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE

                    } else {

                        layoutEmpty.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )
    }

    private fun deleteVehicle(vehicle: Vehicle) {

        database
            .child(vehicle.vehicleNumber)
            .removeValue()

        Toast.makeText(
            this,
            "Vehicle Deleted",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun filterVehicles(query: String) {

        val filtered = ArrayList<Vehicle>()

        for (vehicle in allVehicles) {

            if (
                vehicle.vehicleNumber.contains(query, true)
                ||
                vehicle.bikeName.contains(query, true)
            ) {

                filtered.add(vehicle)
            }
        }

        recyclerView.adapter =
            VehicleAdapter(filtered) { vehicle ->
                deleteVehicle(vehicle)
            }
    }

    private fun showDeleteDialog(vehicle: Vehicle) {

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("⚠ Delete Vehicle")
            .setMessage(
                "Are you sure you want to delete\n\n${vehicle.vehicleNumber}\n\nThis action cannot be undone."
            )
            .setPositiveButton("Delete") { _, _ ->

                deleteVehicle(vehicle)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}