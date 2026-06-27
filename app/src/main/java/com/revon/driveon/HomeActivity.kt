package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {

    lateinit var dotsLayout: LinearLayout
    lateinit var activeBookingCard: CardView
    lateinit var txtActiveVehicle: TextView
    lateinit var txtActiveDate: TextView
    lateinit var profileName: TextView
    lateinit var profilePhone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        profileName = findViewById(R.id.profileName)
        profilePhone = findViewById(R.id.profilePhone)

        activeBookingCard =
            findViewById(R.id.activeBookingCard)

        txtActiveVehicle =
            findViewById(R.id.txtActiveVehicle)

        txtActiveDate =
            findViewById(R.id.txtActiveDate)

        loadUserData()
        checkActiveBooking()

        dotsLayout = findViewById(R.id.dotsLayout)

        val btnBookVehicle = findViewById<CardView>(R.id.btnBookVehicle)

        val updatesPager =
            findViewById<ViewPager2>(R.id.updatesPager)

        loadHomepagePosts(updatesPager)

        btnBookVehicle.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    VehicleEntryActivity::class.java
                )
            )
        }

        val btnMenu =
            findViewById<LinearLayout>(
                R.id.btnMenu
            )

        btnMenu.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MenuActivity::class.java
                )
            )
        }
    }

    private fun checkActiveBooking() {

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
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        var foundBooking = false

                        for (booking in snapshot.children) {

                            val userPhone =
                                booking.child("userPhone")
                                    .getValue(String::class.java)
                                    ?: ""

                            val status =
                                booking.child("status")
                                    .getValue(String::class.java)
                                    ?: ""

                            if (
                                userPhone == phone &&
                                status == "ACTIVE"
                            ) {

                                foundBooking = true

                                findViewById<CardView>(
                                    R.id.btnBookVehicle
                                ).visibility =
                                    android.view.View.GONE

                                activeBookingCard.visibility =
                                    android.view.View.VISIBLE

                                activeBookingCard.setOnClickListener {

                                    val intent =
                                        Intent(
                                            this@HomeActivity,
                                            ReturnVehicleActivity::class.java
                                        )

                                    intent.putExtra(
                                        "bookingId",
                                        booking.key
                                    )

                                    startActivity(intent)
                                }

                                txtActiveVehicle.text =
                                    "Vehicle : " +
                                            (
                                                    booking.child(
                                                        "vehicleNumber"
                                                    )
                                                        .getValue(String::class.java)
                                                        ?: ""
                                                    )

                                txtActiveDate.text =
                                    "Started : " +
                                            (
                                                    booking.child(
                                                        "date"
                                                    )
                                                        .getValue(String::class.java)
                                                        ?: ""
                                                    )

                                break
                            }
                        }

                        if (!foundBooking) {

                            findViewById<CardView>(
                                R.id.btnBookVehicle
                            ).visibility =
                                android.view.View.VISIBLE

                            activeBookingCard.visibility =
                                android.view.View.GONE
                        }
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {
                    }
                }
            )
    }

    private fun loadHomepagePosts(
        updatesPager: ViewPager2
    ) {

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("homepagePosts")
            .get()
            .addOnSuccessListener { snapshot ->

                val updates =
                    mutableListOf<UpdateItem>()

                for(post in snapshot.children) {

                    val imageUrl =
                        post.child("imageUrl")
                            .getValue(String::class.java)
                            ?: ""

                    val title =
                        post.child("title")
                            .getValue(String::class.java)
                            ?: ""

                    val description =
                        post.child("description")
                            .getValue(String::class.java)
                            ?: ""

                    updates.add(

                        UpdateItem(
                            imageUrl,
                            title,
                            description
                        )
                    )
                }

                updatesPager.adapter =
                    UpdatesAdapter(updates)

                createDots(updates.size)

                updatesPager.registerOnPageChangeCallback(
                    object : ViewPager2.OnPageChangeCallback() {

                        override fun onPageSelected(
                            position: Int
                        ) {

                            super.onPageSelected(position)

                            updateDots(position)
                        }
                    }
                )
            }
    }

    private fun loadUserData() {

        val prefs = getSharedPreferences(
            "DriveOnPrefs",
            MODE_PRIVATE
        )

        val phone =
            prefs.getString("phone", "") ?: ""

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("userprofiles")
            .child(phone)
            .get()
            .addOnSuccessListener { snapshot ->

                val name =
                    snapshot.child("name")
                        .getValue(String::class.java)
                        ?: ""

                val number =
                    snapshot.child("phone")
                        .getValue(String::class.java)
                        ?: ""

                profileName.text = name
                profilePhone.text = number
            }
    }

    private fun createDots(count: Int) {

        dotsLayout.removeAllViews()

        for (i in 0 until count) {

            val dot = TextView(this)

            dot.text = "●"
            dot.textSize = 14f
            dot.setPadding(8, 0, 8, 0)

            if (i == 0)
                dot.setTextColor(android.graphics.Color.parseColor("#FDD104"))
            else
                dot.setTextColor(android.graphics.Color.LTGRAY)

            dotsLayout.addView(dot)
        }
    }

    private fun updateDots(position: Int) {

        for (i in 0 until dotsLayout.childCount) {

            val dot =
                dotsLayout.getChildAt(i) as TextView

            if (i == position)
                dot.setTextColor(android.graphics.Color.parseColor("#FDD104"))
            else
                dot.setTextColor(android.graphics.Color.LTGRAY)
        }
    }

}