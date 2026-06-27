package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val menuBookingHistory =
            findViewById<LinearLayout>(R.id.menuBookingHistory)

        val menuProfile =
            findViewById<LinearLayout>(R.id.menuProfile)

        val menuHelp =
            findViewById<LinearLayout>(R.id.menuHelp)

        val menuLogout =
            findViewById<LinearLayout>(R.id.menuLogout)


        // Load Google Profile Photo
        val photoUrl = GoogleSignIn
            .getLastSignedInAccount(this)
            ?.photoUrl


        menuBookingHistory.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    BookingHistoryActivity::class.java
                )
            )
        }

        menuProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        menuHelp.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    HelpSupportActivity::class.java
                )
            )
        }

        menuLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        // Sign out of Firebase
        FirebaseAuth.getInstance().signOut()

        // Sign out of Google
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestEmail()
            .build()

        val googleSignInClient: GoogleSignInClient =
            GoogleSignIn.getClient(this, gso)

        googleSignInClient.signOut().addOnCompleteListener {
            // Clear saved session data
            getSharedPreferences("DriveOnPrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            // Return to the login screen, clearing the back stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
