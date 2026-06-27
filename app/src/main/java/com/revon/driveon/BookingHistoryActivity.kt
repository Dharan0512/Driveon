package com.revon.driveon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class BookingHistoryActivity :
    AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_booking_history
        )

        val recycler =
            findViewById<RecyclerView>(
                R.id.recyclerBookings
            )

        recycler.layoutManager =
            LinearLayoutManager(this)

        val prefs =
            getSharedPreferences(
                "DriveOnPrefs",
                MODE_PRIVATE
            )

        val phone =
            prefs.getString("phone", "")
                ?: ""

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("bookings")
            .get()
            .addOnSuccessListener { snapshot ->

                val list =
                    ArrayList<Booking>()

                for(item in snapshot.children){

                    val booking =
                        item.getValue(
                            Booking::class.java
                        )

                    if(
                        booking?.userPhone ==
                        phone
                    ){

                        booking?.let {
                            list.add(it)
                        }
                    }
                }

                recycler.adapter =
                    BookingAdapter(list)
            }
    }
}