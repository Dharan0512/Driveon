package com.revon.driveon

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class RejectedUserDetailsActivity : AppCompatActivity() {

    private lateinit var txtPhone: TextView
    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtReason: TextView

    private lateinit var btnMoveToPending: Button
    private lateinit var btnApproveUser: Button

    private lateinit var database: DatabaseReference

    private var phone = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_rejected_user_details
        )

        phone =
            intent.getStringExtra("phone") ?: ""

        txtPhone =
            findViewById(R.id.txtPhone)

        txtName =
            findViewById(R.id.txtName)

        txtEmail =
            findViewById(R.id.txtEmail)

        txtReason =
            findViewById(R.id.txtReason)

        btnMoveToPending =
            findViewById(R.id.btnMoveToPending)

        btnApproveUser =
            findViewById(R.id.btnApproveUser)

        database =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("userprofiles")

        loadUser()

        btnMoveToPending.setOnClickListener {

            database.child(phone)
                .child("kycStatus")
                .setValue("pending")

            Toast.makeText(
                this,
                "Moved to Pending",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        btnApproveUser.setOnClickListener {

            database.child(phone)
                .child("kycStatus")
                .setValue("approved")

            Toast.makeText(
                this,
                "Approved Successfully",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }

    private fun loadUser() {

        database.child(phone)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        txtPhone.text =
                            "Phone : $phone"

                        txtName.text =
                            "Name : ${
                                snapshot.child("name")
                                    .getValue(String::class.java)
                                    ?: "-"
                            }"

                        txtEmail.text =
                            "Email : ${
                                snapshot.child("email")
                                    .getValue(String::class.java)
                                    ?: "-"
                            }"

                        txtReason.text =
                            "Reason : ${
                                snapshot.child("rejectionReason")
                                    .getValue(String::class.java)
                                    ?: "-"
                            }"
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {}
                })
    }
}