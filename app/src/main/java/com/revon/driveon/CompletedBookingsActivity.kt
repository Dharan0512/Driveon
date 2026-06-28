package com.revon.driveon

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class CompletedBookingsActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnClearSearch: ImageView
    private lateinit var txtBookingCount: TextView
    private lateinit var emptyState: View

    private lateinit var adapter: AdminBookingAdapter

    private val allBookings = ArrayList<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_completed_bookings)

        recycler = findViewById(R.id.recyclerBookings)
        etSearch = findViewById(R.id.etSearch)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        txtBookingCount = findViewById(R.id.txtBookingCount)
        emptyState = findViewById(R.id.emptyState)

        findViewById<ImageView>(R.id.btnBack)
            .setOnClickListener { finish() }

        adapter = AdminBookingAdapter(ArrayList())
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnClearSearch.setOnClickListener { etSearch.setText("") }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {
                btnClearSearch.visibility =
                    if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        loadBookings()
    }

    private fun loadBookings() {

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("bookings")
            .get()
            .addOnSuccessListener {

                allBookings.clear()

                for (item in it.children) {

                    val booking = item.getValue(Booking::class.java)

                    if (booking?.status == "COMPLETED") {
                        allBookings.add(booking)
                    }
                }

                filter(etSearch.text.toString())
            }
    }

    private fun filter(query: String) {

        val filtered = allBookings.filter {
            it.bikeName.contains(query, true) ||
                it.vehicleNumber.contains(query, true) ||
                it.userPhone.contains(query, true)
        }

        adapter.updateData(filtered)

        txtBookingCount.text = filtered.size.toString()

        val isEmpty = filtered.isEmpty()
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}
