package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class RegisteredUserDetailsActivity : AppCompatActivity() {

    private lateinit var txtPhone: TextView
    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtProfileCompletion: TextView

    private lateinit var btnLicenseFront: Button
    private lateinit var btnLicenseBack: Button
    private lateinit var btnAadhaarFront: Button
    private lateinit var btnAadhaarBack: Button

    private lateinit var txtKycStatus: TextView

    private lateinit var txtEmergency1: TextView
    private lateinit var txtEmergency2: TextView
    private lateinit var txtEmergency3: TextView

    private lateinit var btnWorkingFront: Button
    private lateinit var btnWorkingBack: Button

    private lateinit var btnResidentialProof: Button
    private lateinit var btnOtherDocument: Button

    private lateinit var database: DatabaseReference

    private var phone = ""

    private var licenseFrontUrl = ""
    private var licenseBackUrl = ""
    private var aadhaarFrontUrl = ""
    private var aadhaarBackUrl = ""

    private var workingFrontUrl = ""
    private var workingBackUrl = ""

    private var residentialProofUrl = ""
    private var otherDocumentUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_registered_user_details
        )

        phone =
            intent.getStringExtra("phone") ?: ""

        txtPhone =
            findViewById(R.id.txtPhone)

        txtName =
            findViewById(R.id.txtName)

        txtEmail =
            findViewById(R.id.txtEmail)

        txtProfileCompletion =
            findViewById(R.id.txtProfileCompletion)

        btnLicenseFront =
            findViewById(R.id.btnLicenseFront)

        btnLicenseBack =
            findViewById(R.id.btnLicenseBack)

        btnAadhaarFront =
            findViewById(R.id.btnAadhaarFront)

        btnAadhaarBack =
            findViewById(R.id.btnAadhaarBack)

        txtProfileCompletion =
            findViewById(R.id.txtProfileCompletion)

        txtKycStatus =
            findViewById(R.id.txtKycStatus)

        txtEmergency1 =
            findViewById(R.id.txtEmergency1)

        txtEmergency2 =
            findViewById(R.id.txtEmergency2)

        txtEmergency3 =
            findViewById(R.id.txtEmergency3)

        btnLicenseFront =
            findViewById(R.id.btnLicenseFront)

        btnLicenseBack =
            findViewById(R.id.btnLicenseBack)

        btnAadhaarFront =
            findViewById(R.id.btnAadhaarFront)

        btnAadhaarBack =
            findViewById(R.id.btnAadhaarBack)

        btnWorkingFront =
            findViewById(R.id.btnWorkingFront)

        btnWorkingBack =
            findViewById(R.id.btnWorkingBack)

        btnResidentialProof =
            findViewById(R.id.btnResidentialProof)

        btnOtherDocument =
            findViewById(R.id.btnOtherDocument)

        database =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("userprofiles")

        loadUserDetails()

        btnLicenseFront.setOnClickListener {
            Toast.makeText(
                this,
                licenseFrontUrl,
                Toast.LENGTH_LONG
            ).show()
            openImage(licenseFrontUrl)
        }

        btnLicenseBack.setOnClickListener {
            openImage(licenseBackUrl)
        }

        btnAadhaarFront.setOnClickListener {
            openImage(aadhaarFrontUrl)
        }

        btnAadhaarBack.setOnClickListener {
            openImage(aadhaarBackUrl)
        }

        btnWorkingFront.setOnClickListener {
            openImage(workingFrontUrl)
        }

        btnWorkingBack.setOnClickListener {
            openImage(workingBackUrl)
        }

        btnResidentialProof.setOnClickListener {
            openImage(residentialProofUrl)
        }

        btnOtherDocument.setOnClickListener {
            openImage(otherDocumentUrl)
        }
    }

    private fun loadUserDetails() {

        database.child(phone)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        txtPhone.text = "Phone : $phone"

                        txtName.text =
                            "Name : ${
                                snapshot.child("name")
                                    .getValue(String::class.java) ?: "-"
                            }"

                        txtEmail.text =
                            "Email : ${
                                snapshot.child("email")
                                    .getValue(String::class.java) ?: "-"
                            }"

                        txtProfileCompletion.text =
                            "Profile Completion : ${
                                snapshot.child("profileCompletion")
                                    .getValue(Int::class.java) ?: 0
                            }%"

                        txtKycStatus.text =
                            "KYC Status : ${
                                snapshot.child("kycStatus")
                                    .getValue(String::class.java) ?: "-"
                            }"

                        txtEmergency1.text =
                            "Emergency Contact 1 : ${
                                snapshot.child("contacts")
                                    .child("contact1")
                                    .getValue(String::class.java) ?: "-"
                            }"

                        txtEmergency2.text =
                            "Emergency Contact 2 : ${
                                snapshot.child("contacts")
                                    .child("contact2")
                                    .getValue(String::class.java) ?: "-"
                            }"

                        txtEmergency3.text =
                            "Emergency Contact 3 : ${
                                snapshot.child("contacts")
                                    .child("contact3")
                                    .getValue(String::class.java) ?: "-"
                            }"

                        licenseFrontUrl =
                            snapshot.child("documents")
                                .child("license_front")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        licenseBackUrl =
                            snapshot.child("documents")
                                .child("license_back")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        aadhaarFrontUrl =
                            snapshot.child("documents")
                                .child("aadhaar_front")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        aadhaarBackUrl =
                            snapshot.child("documents")
                                .child("aadhaar_back")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        workingFrontUrl =
                            snapshot.child("documents")
                                .child("working_front")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        workingBackUrl =
                            snapshot.child("documents")
                                .child("working_back")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        residentialProofUrl =
                            snapshot.child("documents")
                                .child("residential_proof")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        otherDocumentUrl =
                            snapshot.child("documents")
                                .child("other_document")
                                .child("url")
                                .getValue(String::class.java) ?: ""
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {}
                })
    }

    private fun openImage(url: String) {

        if(url.isEmpty()) {

            Toast.makeText(
                this,
                "Document not available",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val intent = Intent(
            this,
            ImagePreviewActivity::class.java
        )

        intent.putExtra(
            "imageUrl",
            url
        )

        startActivity(intent)
    }
}