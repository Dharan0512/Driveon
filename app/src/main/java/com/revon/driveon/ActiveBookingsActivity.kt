package com.revon.driveon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ActiveBookingsActivity :
    AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_booking_management
        )

        val recycler =
            findViewById<RecyclerView>(
                R.id.recyclerBookings
            )

        recycler.layoutManager =
            LinearLayoutManager(this)

        val list =
            ArrayList<Booking>()

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("bookings")
            .get()
            .addOnSuccessListener {

                for(item in it.children){

                    val booking =
                        item.getValue(
                            Booking::class.java
                        )

                    if (
                        booking?.status ==
                        "ACTIVE"
                    ){

                        booking?.let {
                            list.add(it)
                        }
                    }
                }

                recycler.adapter =
                    BillAdapter(list)
            }
    }
}