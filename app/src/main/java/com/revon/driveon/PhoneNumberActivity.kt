package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PhoneNumberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number)

        val phoneInput =
            findViewById<EditText>(
                R.id.phoneInput
            )

        val continueBtn =
            findViewById<Button>(
                R.id.continueBtn
            )

        continueBtn.setOnClickListener {

            val phone =
                phoneInput.text.toString().trim()

            if (phone.isEmpty()) {

                Toast.makeText(
                    this,
                    "Enter Phone Number",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val user =
                FirebaseAuth.getInstance().currentUser

            if (user != null) {

                val userMap =
                    HashMap<String, Any>()

                userMap["name"] =
                    user.displayName ?: ""

                userMap["email"] =
                    user.email ?: ""

                userMap["phone"] =
                    phone

                userMap["profile_photo"] =
                    user.photoUrl.toString()

                userMap["created_time"] =
                    System.currentTimeMillis()

                userMap["last_login"] =
                    System.currentTimeMillis()

                userMap["live_location"] =
                    "not_updated"

                userMap["contacts_uploaded"] =
                    false

                userMap["device_model"] =
                    android.os.Build.MODEL

                FirebaseDatabase.getInstance()
                    .getReference("Driveon")
                    .child("userprofiles")
                    .child(phone)
                    .setValue(userMap)

                val email =
                    user.email ?: ""

                val emailKey =
                    email.replace(".", ",")

                FirebaseDatabase.getInstance()
                    .getReference("Driveon")
                    .child("useremails")
                    .child(emailKey)
                    .child("phone")
                    .setValue(phone)

                val prefs = getSharedPreferences(
                    "DriveOnPrefs",
                    MODE_PRIVATE
                )

                prefs.edit()
                    .putString("phone", phone)
                    .apply()

                startActivity(
                    Intent(
                        this,
                        HomeActivity::class.java
                    )
                )

                finish()
            }
        }
    }
}