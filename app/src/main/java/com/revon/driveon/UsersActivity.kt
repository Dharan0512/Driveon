package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class UsersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_users)

        findViewById<MaterialCardView>(R.id.btnPendingUsers)
            .setOnClickListener {
                startActivity(
                    Intent(this, PendingUsersActivity::class.java)
                )
            }

        findViewById<MaterialCardView>(R.id.btnRegisteredUsers)
            .setOnClickListener {
                startActivity(
                    Intent(this, RegisteredUsersActivity::class.java)
                )
            }

        findViewById<MaterialCardView>(R.id.btnRejectedUsers)
            .setOnClickListener {
                startActivity(
                    Intent(this, RejectedUsersActivity::class.java)
                )
            }
    }
}