package com.revon.driveon

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtPhone: TextView
    private lateinit var profileProgress: ProgressBar
    private lateinit var txtPercentage: TextView

    private lateinit var btnLicenseFront: Button
    private lateinit var btnLicenseBack: Button
    private lateinit var btnAadhaarFront: Button
    private lateinit var btnAadhaarBack: Button
    private lateinit var btnWorkFront: Button
    private lateinit var btnWorkBack: Button
    private lateinit var btnAddressProof: Button
    private lateinit var btnOtherDocument: Button

    private lateinit var btnEditName: ImageView
    private lateinit var btnSubmitKyc: Button

    private var kycStatus = "not_started"
    private var kycSubmitted = false

    private lateinit var statusLicenseFront: TextView
    private lateinit var statusLicenseBack: TextView
    private lateinit var statusAadhaarFront: TextView
    private lateinit var statusAadhaarBack: TextView
    private lateinit var statusWorkFront: TextView
    private lateinit var statusWorkBack: TextView
    private lateinit var statusAddress: TextView
    private lateinit var statusOther: TextView

    lateinit var txtKycStatus: TextView



    private var currentDocument = ""
    private var userPhone = ""


    // URLs
    private var licenseFrontUrl = ""
    private var licenseBackUrl = ""
    private var aadhaarFrontUrl = ""
    private var aadhaarBackUrl = ""
    private var workFrontUrl = ""
    private var workBackUrl = ""
    private var addressProofUrl = ""
    private var otherDocumentUrl = ""

    // STATUS SYSTEM (NEW)
    private var licenseFrontStatus = "pending"
    private var licenseBackStatus = "pending"
    private var aadhaarFrontStatus = "pending"
    private var aadhaarBackStatus = "pending"
    private var workFrontStatus = "pending"
    private var workBackStatus = "pending"
    private var addressStatus = "pending"
    private var otherStatus = "pending"

    // IMAGE PICKER
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uploadDocument(it, currentDocument) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (kycStatus == "pending" || kycStatus == "approved") {
            lockKyc()
        }

        txtKycStatus =
            findViewById(R.id.txtKycStatus)

        val menuEmergencyContacts =
            findViewById<LinearLayout>(R.id.menuEmergencyContacts)

        menuEmergencyContacts.setOnClickListener {

            val intent = Intent(
                this,

                ContactsActivity::class.java
            )

            intent.putExtra(
                "userPhone",
                userPhone
            )

            if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {

                startActivity(intent)

            } else {

                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_CONTACTS),
                    1001
                )
            }
        }



        initViews()
        setupClicks()
        loadProfile()
    }

    // ---------------- INIT ----------------
    private fun initViews() {
        profileImage = findViewById(R.id.profileImage)
        txtName = findViewById(R.id.txtName)
        txtEmail = findViewById(R.id.txtEmail)
        txtPhone = findViewById(R.id.txtPhone)
        profileProgress = findViewById(R.id.profileProgress)
        txtPercentage = findViewById(R.id.txtPercentage)

        btnLicenseFront = findViewById(R.id.btnLicenseFront)
        btnLicenseBack = findViewById(R.id.btnLicenseBack)
        btnAadhaarFront = findViewById(R.id.btnAadhaarFront)
        btnAadhaarBack = findViewById(R.id.btnAadhaarBack)
        btnWorkFront = findViewById(R.id.btnWorkFront)
        btnWorkBack = findViewById(R.id.btnWorkBack)
        btnAddressProof = findViewById(R.id.btnAddressProof)
        btnOtherDocument = findViewById(R.id.btnOtherDocument)
        btnEditName = findViewById(R.id.btnEditName)
        btnSubmitKyc = findViewById(R.id.btnSubmitKyc)

        statusLicenseFront = findViewById(R.id.statusLicenseFront)
        statusLicenseBack = findViewById(R.id.statusLicenseBack)
        statusAadhaarFront = findViewById(R.id.statusAadhaarFront)
        statusAadhaarBack = findViewById(R.id.statusAadhaarBack)
        statusWorkFront = findViewById(R.id.statusWorkFront)
        statusWorkBack = findViewById(R.id.statusWorkBack)
        statusAddress = findViewById(R.id.statusAddress)
        statusOther = findViewById(R.id.statusOther)

        btnEditName.setOnClickListener {
            if (kycStatus == "pending" || kycStatus == "approved") {
                Toast.makeText(
                    this,
                    "Name cannot be changed after KYC submission",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showEditNameDialog()
            }
        }

        btnSubmitKyc.setOnClickListener {
            submitKyc()
        }
    }

    private fun showEditNameDialog() {

        val editText = EditText(this)
        editText.setText(txtName.text.toString())

        android.app.AlertDialog.Builder(this)
            .setTitle("Change Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->

                val newName = editText.text.toString().trim()

                if (newName.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Name cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                txtName.text = newName

                FirebaseDatabase.getInstance()
                    .getReference("Driveon")
                    .child("userprofiles")
                    .child(userPhone)
                    .child("name")
                    .setValue(newName)

                Toast.makeText(
                    this,
                    "Name Updated",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun lockKyc() {

        btnEditName.isEnabled = false

        btnSubmitKyc.visibility = android.view.View.GONE
    }

    private fun submitKyc() {

        if (userPhone.isEmpty()) return

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("userprofiles")
            .child(userPhone)
            .child("kycStatus")
            .setValue("pending")

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("userprofiles")
            .child(userPhone)
            .child("kycSubmitted")
            .setValue(true)

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("userprofiles")
            .child(userPhone)
            .child("registeredAt")
            .setValue(System.currentTimeMillis())

        kycStatus = "pending"
        kycSubmitted = true

        btnEditName.visibility = View.GONE

        lockKyc()

        Toast.makeText(
            this,
            "KYC Submitted Successfully",
            Toast.LENGTH_LONG
        ).show()
    }

    // ---------------- CLICK ----------------
    private fun setupClicks() {
        btnLicenseFront.setOnClickListener { handleClick("license_front", licenseFrontUrl) }
        btnLicenseBack.setOnClickListener { handleClick("license_back", licenseBackUrl) }
        btnAadhaarFront.setOnClickListener { handleClick("aadhaar_front", aadhaarFrontUrl) }
        btnAadhaarBack.setOnClickListener { handleClick("aadhaar_back", aadhaarBackUrl) }
        btnWorkFront.setOnClickListener { handleClick("work_front", workFrontUrl) }
        btnWorkBack.setOnClickListener { handleClick("work_back", workBackUrl) }
        btnAddressProof.setOnClickListener { handleClick("address", addressProofUrl) }
        btnOtherDocument.setOnClickListener { handleClick("other", otherDocumentUrl) }
    }

    private fun handleClick(type: String, url: String) {

        if (url.isNotEmpty()) {
            openImagePreview(url)
            return
        }

        if (kycStatus == "pending" || kycStatus == "approved") {
            Toast.makeText(
                this,
                "KYC already submitted",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        currentDocument = type
        imagePicker.launch("image/*")
    }

    // ---------------- IMAGE PREVIEW (FIXED UX) ----------------
    private fun openImagePreview(url: String) {
        val intent = Intent(this, ImagePreviewActivity::class.java)
        intent.putExtra("image_url", url)
        startActivity(intent)
    }

    // ---------------- COMPRESS ----------------
    private fun compressImage(uri: Uri): ByteArray {
        val input = contentResolver.openInputStream(uri) ?: return byteArrayOf()
        val bitmap = BitmapFactory.decodeStream(input) ?: return byteArrayOf()

        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, output)
        return output.toByteArray()
    }

    // ---------------- UPLOAD ----------------
    private fun uploadDocument(uri: Uri, type: String) {

        if (userPhone.isEmpty()) return

        val btn = getButton(type)
        btn?.text = "Uploading..."
        btn?.isEnabled = false

        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("customer_documents")
            .child(userPhone)
            .child("${userPhone}_${type}.jpg")

        val data = compressImage(uri)

        storageRef.putBytes(data)
            .addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener { url ->

                    val downloadUrl = url.toString()

                    saveDocumentUrl(type, downloadUrl)

                    when(type){
                        "license_front" -> licenseFrontUrl = downloadUrl
                        "license_back" -> licenseBackUrl = downloadUrl
                        "aadhaar_front" -> aadhaarFrontUrl = downloadUrl
                        "aadhaar_back" -> aadhaarBackUrl = downloadUrl
                        "work_front" -> workFrontUrl = downloadUrl
                        "work_back" -> workBackUrl = downloadUrl
                        "address" -> addressProofUrl = downloadUrl
                        "other" -> otherDocumentUrl = downloadUrl
                    }

                    updateUI()
                    updateProgress()

                    btn?.isEnabled = true
                }
            }
            .addOnFailureListener {
                btn?.text = "Upload Failed"
                btn?.isEnabled = true
            }

    }

    // ---------------- SAVE ----------------
    private fun saveDocumentUrl(type: String, url: String) {

        val ref = FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("userprofiles")
            .child(userPhone)
            .child("documents")
            .child(type)

        val data = mapOf(
            "url" to url,
            "status" to "pending",
            "uploadedAt" to System.currentTimeMillis().toString()
        )

        ref.setValue(data)
    }

    // ---------------- LOAD PROFILE ----------------
    private fun loadProfile() {

        val user = FirebaseAuth.getInstance().currentUser ?: return
        val email = user.email ?: ""


        val ref = FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("userprofiles")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (snap in snapshot.children) {

                    val completion =
                        snap.child("profileCompletion")
                            .getValue(Int::class.java)
                            ?: 0

                    kycStatus =
                        snap.child("kycStatus")
                            .getValue(String::class.java)
                            ?: "not_started"

                    kycSubmitted =
                        snap.child("kycSubmitted")
                            .getValue(Boolean::class.java)
                            ?: false



                    val dbEmail = snap.child("email").getValue(String::class.java) ?: ""

                    if (dbEmail.equals(email, true)) {

                        val name = snap.child("name").getValue(String::class.java) ?: ""
                        val phone = snap.child("phone").getValue(String::class.java) ?: ""
                        val photo = snap.child("profile_photo").getValue(String::class.java) ?: ""

                        userPhone = phone

                        val doc = snap.child("documents")

                        txtKycStatus.text = "KYC Status : ${kycStatus.uppercase()}"

                        licenseFrontUrl =
                            doc.child("license_front")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        licenseBackUrl =
                            doc.child("license_back")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        aadhaarFrontUrl =
                            doc.child("aadhaar_front")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        aadhaarBackUrl =
                            doc.child("aadhaar_back")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        workFrontUrl =
                            doc.child("work_front")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        workBackUrl =
                            doc.child("work_back")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        addressProofUrl =
                            doc.child("address")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        otherDocumentUrl =
                            doc.child("other")
                                .child("url")
                                .getValue(String::class.java) ?: ""

                        txtName.text = name
                        txtEmail.text = dbEmail
                        txtPhone.text = phone

                        if (photo.isNotEmpty()) {
                            Glide.with(this@ProfileActivity)
                                .load(photo)
                                .circleCrop()
                                .into(profileImage)
                        }

                        updateUI()
                        updateProgress()

                        return
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ---------------- UI UPDATE ----------------
    private fun updateUI() {

        setButton(btnLicenseFront, licenseFrontUrl, "License Front")
        setButton(btnLicenseBack, licenseBackUrl, "License Back")
        setButton(btnAadhaarFront, aadhaarFrontUrl, "Aadhaar Front")
        setButton(btnAadhaarBack, aadhaarBackUrl, "Aadhaar Back")
        setButton(btnWorkFront, workFrontUrl, "Work Front")
        setButton(btnWorkBack, workBackUrl, "Work Back")
        setButton(btnAddressProof, addressProofUrl, "Address Proof")
        setButton(btnOtherDocument, otherDocumentUrl, "Other Document")

        statusLicenseFront.text =
            if (licenseFrontUrl.isNotEmpty()) "UPLOADED" else "PENDING"

        statusLicenseBack.text =
            if (licenseBackUrl.isNotEmpty()) "UPLOADED" else "PENDING"

        statusAadhaarFront.text =
            if (aadhaarFrontUrl.isNotEmpty()) "UPLOADED" else "PENDING"

        statusAadhaarBack.text =
            if (aadhaarBackUrl.isNotEmpty()) "UPLOADED" else "PENDING"

        statusWorkFront.text =
            if (workFrontUrl.isNotEmpty()) "UPLOADED" else "PENDING"

        statusWorkBack.text =
            if (workBackUrl.isNotEmpty()) "UPLOADED" else "PENDING"

        statusAddress.text =
            if (addressProofUrl.isNotEmpty()) "UPLOADED" else "PENDING"

        statusOther.text =
            if (otherDocumentUrl.isNotEmpty()) "UPLOADED" else "PENDING"
    }

    private fun setButton(btn: Button, url: String, label: String) {

        if (url.isNotEmpty()) {
            btn.text = "OPEN"
        }
    }

    // ---------------- PROGRESS ----------------
    private fun updateProgress() {

        var progress = 0

        if (txtName.text.toString().trim().isNotEmpty()) progress += 10

        if (licenseFrontUrl.isNotEmpty()) progress += 15
        if (licenseBackUrl.isNotEmpty()) progress += 15

        if (aadhaarFrontUrl.isNotEmpty()) progress += 15
        if (aadhaarBackUrl.isNotEmpty()) progress += 15

        if (workFrontUrl.isNotEmpty()) progress += 10
        if (workBackUrl.isNotEmpty()) progress += 10

        if (addressProofUrl.isNotEmpty()) progress += 10

        if (progress > 100) progress = 100

        profileProgress.progress = progress
        txtPercentage.text = "$progress% Complete"

        if (progress == 100 &&
            kycStatus != "pending" &&
            kycStatus != "approved"
        ) {
            btnSubmitKyc.visibility = android.view.View.VISIBLE
        } else {
            btnSubmitKyc.visibility = android.view.View.GONE
        }
    }

    // ---------------- HELPER ----------------
    private fun getButton(type: String): Button? {
        return when (type) {
            "license_front" -> btnLicenseFront
            "license_back" -> btnLicenseBack
            "aadhaar_front" -> btnAadhaarFront
            "aadhaar_back" -> btnAadhaarBack
            "work_front" -> btnWorkFront
            "work_back" -> btnWorkBack
            "address" -> btnAddressProof
            "other" -> btnOtherDocument
            else -> null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 1001) {

            if (grantResults.isNotEmpty()
                && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {

                val intent = Intent(
                    this,
                    ContactsActivity::class.java
                )

                intent.putExtra(
                    "userPhone",
                    userPhone
                )

                startActivity(intent)

            } else {

                Toast.makeText(
                    this,
                    "Contacts permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}