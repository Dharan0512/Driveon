package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.android.material.button.MaterialButton

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(
                getString(R.string.default_web_client_id)
            )
            .requestEmail()
            .build()

        googleSignInClient =
            GoogleSignIn.getClient(this, gso)

        val googleBtn =
            findViewById<MaterialButton>(
                R.id.googleBtn
            )

        val adminBtn =
            findViewById<Button>(
                R.id.adminBtn
            )

        googleBtn.setOnClickListener {

            val signInIntent =
                googleSignInClient.signInIntent

            startActivityForResult(
                signInIntent,
                100
            )
        }

        adminBtn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AdminActivity::class.java
                )
            )
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == 100) {

            val task =
                GoogleSignIn
                    .getSignedInAccountFromIntent(data)

            try {

                val account =
                    task.getResult(ApiException::class.java)

                val credential =
                    GoogleAuthProvider.getCredential(
                        account.idToken,
                        null
                    )

                auth.signInWithCredential(credential)

                    .addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {


                            val user =
                                auth.currentUser

                            if (user != null) {

                                val phone =
                                    user.phoneNumber
                                        ?: "Number not added"

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

                                val email =
                                    user.email ?: ""

                                val emailKey =
                                    email.replace(".", ",")

                                FirebaseDatabase.getInstance()
                                    .getReference("Driveon")
                                    .child("useremails")
                                    .child(emailKey)
                                    .get()
                                    .addOnSuccessListener { snapshot ->

                                        if (snapshot.exists()) {

                                            val phone =
                                                snapshot.child("phone")
                                                    .getValue(String::class.java)
                                                    ?: ""

                                            val prefs =
                                                getSharedPreferences(
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

                                        } else {

                                            startActivity(
                                                Intent(
                                                    this,
                                                    PhoneNumberActivity::class.java
                                                )
                                            )

                                            finish()
                                        }
                                    }
                            }

                        } else {

                            Toast.makeText(
                                this,
                                "Login Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            } catch (e: Exception) {

                Toast.makeText(
                    this,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}