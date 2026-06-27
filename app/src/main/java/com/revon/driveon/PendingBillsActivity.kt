package com.revon.driveon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class PendingBillsActivity :
    AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_pending_bills
        )

        val recycler =
            findViewById<RecyclerView>(
                R.id.recyclerBills
            )

        recycler.layoutManager =
            LinearLayoutManager(this)

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("bookings")
            .get()
            .addOnSuccessListener {

                val list =
                    ArrayList<Booking>()

                for(item in it.children){

                    val booking =
                        item.getValue(
                            Booking::class.java
                        )

                    if(
                        booking?.status ==
                        "PENDING_BILLING"
                    ){

                        booking?.let { b ->

                            list.add(b)
                        }
                    }
                }

                recycler.adapter =
                    BillAdapter(list)
            }
    }
}