package com.revon.driveon

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class BillReviewActivity :
    AppCompatActivity() {

    lateinit var txtVehicle: TextView
    lateinit var txtCustomer: TextView
    lateinit var txtPackage: TextView
    lateinit var txtDistance: TextView
    lateinit var txtCustomRequest: TextView

    lateinit var etGeneratedBill: EditText
    lateinit var etFinalBill: EditText
    lateinit var etRemarks: EditText

    lateinit var btnCompleteBooking: Button

    var bookingId = ""

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_bill_review
        )

        bookingId =
            intent.getStringExtra(
                "bookingId"
            ) ?: ""

        txtVehicle =
            findViewById(R.id.txtVehicle)

        txtCustomer =
            findViewById(R.id.txtCustomer)

        txtPackage =
            findViewById(R.id.txtPackage)

        txtDistance =
            findViewById(R.id.txtDistance)

        txtCustomRequest =
            findViewById(R.id.txtCustomRequest)

        etGeneratedBill =
            findViewById(R.id.etGeneratedBill)

        etFinalBill =
            findViewById(R.id.etFinalBill)

        etRemarks =
            findViewById(R.id.etRemarks)

        btnCompleteBooking =
            findViewById(
                R.id.btnCompleteBooking
            )

        loadBooking()

        btnCompleteBooking.setOnClickListener {

            completeBooking()

        }
    }

    private fun loadBooking() {



        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("bookings")
            .child(bookingId)
            .get()
            .addOnSuccessListener {

                val booking =
                    it.getValue(
                        Booking::class.java
                    ) ?: return@addOnSuccessListener

                txtVehicle.text =
                    booking.vehicleNumber

                txtCustomer.text =
                    booking.userPhone

                txtPackage.text =
                    booking.packageType

                val start =
                    booking.startReading
                        .toIntOrNull() ?: 0

                val end =
                    booking.endReading
                        .toIntOrNull() ?: 0

                val distance =
                    end - start


                if(
                    booking.packageType ==
                    "Custom"
                ){

                    txtCustomRequest.text =

                        "Custom Days : ${
                            booking.customDays
                        }\n\n" +

                                "Requested Price : ₹${
                                    booking.customPrice
                                }\n\n" +

                                "Remarks : ${
                                    booking.customRemarks
                                }"
                }

                val kmLimit =
                    booking.kmLimit
                        .toIntOrNull() ?: 0

                val extraKmRate =
                    booking.extraKmCharge
                        .toIntOrNull() ?: 0

                val basePrice =
                    booking.agreedPrice
                        .toIntOrNull() ?: 0

                val extraKm =

                    if(distance > kmLimit)
                        distance - kmLimit
                    else
                        0

                val extraCharge =
                    extraKm * extraKmRate

                val generatedBill =
                    basePrice + extraCharge

                txtDistance.text =
                    "Distance : $distance KM\n" +
                            "Limit : $kmLimit KM\n" +
                            "Extra KM : $extraKm\n" +
                            "Extra Charge : ₹$extraCharge"

                etGeneratedBill.setText(
                    generatedBill.toString()
                )

                etFinalBill.setText(
                    generatedBill.toString()
                )
            }
    }

    private fun completeBooking() {

        val generatedBill =
            etGeneratedBill.text.toString()

        val finalBill =
            etFinalBill.text.toString()

        val remarks =
            etRemarks.text.toString()

        val bookingRef =

            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("bookings")
                .child(bookingId)

        bookingRef.child(
            "generatedBill"
        ).setValue(
            generatedBill
        )

        bookingRef.child(
            "finalBill"
        ).setValue(
            finalBill
        )

        bookingRef.child(
            "adminRemarks"
        ).setValue(
            remarks
        )

        bookingRef.child(
            "completedDate"
        ).setValue(
            System.currentTimeMillis()
        )

        bookingRef.child(
            "status"
        ).setValue(
            "COMPLETED"
        )

        Toast.makeText(
            this,
            "Booking Completed",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }
}