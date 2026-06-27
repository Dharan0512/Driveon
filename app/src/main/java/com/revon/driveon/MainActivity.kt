package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {

            // USER ALREADY LOGGED IN
            startActivity(Intent(this, HomeActivity::class.java))

        } else {

            // USER NOT LOGGED IN
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }
}