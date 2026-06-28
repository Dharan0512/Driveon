package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class BookingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_bookings)

        findViewById<View>(R.id.btnActiveBookings)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        ActiveBookingsActivity::class.java
                    )
                )
            }

        findViewById<View>(R.id.btnPendingBills)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        BookingManagementActivity::class.java
                    )
                )
            }

        findViewById<View>(R.id.btnCompletedBookings)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        CompletedBookingsActivity::class.java
                    )
                )
            }
    }
}