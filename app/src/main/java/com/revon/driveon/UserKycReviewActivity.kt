package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class UserKycReviewActivity : AppCompatActivity() {

    private lateinit var txtPhone: TextView
    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView

    private lateinit var btnApprove: Button
    private lateinit var btnReject: Button

    private lateinit var btnViewLicenseFront: Button
    private lateinit var btnViewLicenseBack: Button
    private lateinit var btnViewAadhaarFront: Button
    private lateinit var btnViewAadhaarBack: Button

    private lateinit var etRejectReason: EditText

    private lateinit var database: DatabaseReference

    private var phone = ""

    private var licenseFrontUrl = ""
    private var licenseBackUrl = ""
    private var aadhaarFrontUrl = ""
    private var aadhaarBackUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_user_kyc_review
        )

        phone =
            intent.getStringExtra("phone") ?: ""

        txtPhone =
            findViewById(R.id.txtPhone)

        txtName =
            findViewById(R.id.txtName)

        txtEmail =
            findViewById(R.id.txtEmail)

        btnApprove =
            findViewById(R.id.btnApprove)

        btnReject =
            findViewById(R.id.btnReject)

        etRejectReason =
            findViewById(R.id.etRejectReason)

        btnViewLicenseFront =
            findViewById(R.id.btnViewLicenseFront)

        btnViewLicenseBack =
            findViewById(R.id.btnViewLicenseBack)

        btnViewAadhaarFront =
            findViewById(R.id.btnViewAadhaarFront)

        btnViewAadhaarBack =
            findViewById(R.id.btnViewAadhaarBack)

        database =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("userprofiles")

        loadUser()

        btnApprove.setOnClickListener {

            database.child(phone)
                .child("kycStatus")
                .setValue("approved")

            Toast.makeText(
                this,
                "User Approved",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        btnReject.setOnClickListener {

            val reason =
                etRejectReason.text.toString()

            database.child(phone)
                .child("kycStatus")
                .setValue("rejected")

            database.child(phone)
                .child("rejectionReason")
                .setValue(reason)

            Toast.makeText(
                this,
                "User Rejected",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        btnViewLicenseFront.setOnClickListener {
            openImage(licenseFrontUrl)
        }

        btnViewLicenseBack.setOnClickListener {
            openImage(licenseBackUrl)
        }

        btnViewAadhaarFront.setOnClickListener {
            openImage(aadhaarFrontUrl)
        }

        btnViewAadhaarBack.setOnClickListener {
            openImage(aadhaarBackUrl)
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
                            }"

                        txtEmail.text =
                            "Email : ${
                                snapshot.child("email")
                                    .getValue(String::class.java)
                            }"

                        licenseFrontUrl =
                            snapshot.child("documents")
                                .child("license_front")
                                .child("url")
                                .getValue(String::class.java)
                                ?: ""

                        licenseBackUrl =
                            snapshot.child("documents")
                                .child("license_back")
                                .child("url")
                                .getValue(String::class.java)
                                ?: ""

                        aadhaarFrontUrl =
                            snapshot.child("documents")
                                .child("aadhaar_front")
                                .child("url")
                                .getValue(String::class.java)
                                ?: ""

                        aadhaarBackUrl =
                            snapshot.child("documents")
                                .child("aadhaar_back")
                                .child("url")
                                .getValue(String::class.java)
                                ?: ""
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {
                    }
                })
    }

    private fun openImage(url: String) {

        if(url.isEmpty()) return

        val intent =
            Intent(
                this,
                ImagePreviewActivity::class.java
            )

        intent.putExtra(
            "image_url",
            url
        )

        startActivity(intent)
    }
}