package com.revon.driveon

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ReturnVehicleActivity : AppCompatActivity() {

    lateinit var etEndReading: EditText
    lateinit var btnUploadEndPhoto: Button
    lateinit var btnSubmitReturn: Button
    lateinit var imgEndPhoto: ImageView

    var endPhotoUri: Uri? = null

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if(uri != null){

                endPhotoUri = uri

                imgEndPhoto.setImageURI(uri)
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_return_vehicle
        )

        etEndReading =
            findViewById(R.id.etEndReading)

        btnUploadEndPhoto =
            findViewById(R.id.btnUploadEndPhoto)

        btnSubmitReturn =
            findViewById(R.id.btnSubmitReturn)

        imgEndPhoto =
            findViewById(R.id.imgEndPhoto)

        btnUploadEndPhoto.setOnClickListener {

            imagePicker.launch("image/*")

        }

        btnSubmitReturn.setOnClickListener {

            submitReturn()

        }
    }

    private fun submitReturn() {

        val endReading =
            etEndReading.text.toString()

        if(endReading.isEmpty()){

            Toast.makeText(
                this,
                "Enter End Reading",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if(endPhotoUri == null){

            Toast.makeText(
                this,
                "Upload End Photo",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        uploadEndPhoto(
            endReading
        )
    }

    private fun uploadEndPhoto(
        endReading: String
    ) {

        val bookingId =
            intent.getStringExtra(
                "bookingId"
            ) ?: return

        val imageRef =

            FirebaseStorage.getInstance()
                .reference
                .child(
                    "booking_end_photos/$bookingId.jpg"
                )

        imageRef.putFile(
            endPhotoUri!!
        )
            .addOnSuccessListener {

                imageRef.downloadUrl
                    .addOnSuccessListener {

                        updateBooking(
                            bookingId,
                            endReading,
                            it.toString()
                        )
                    }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Upload Failed",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun updateBooking(

        bookingId: String,

        endReading: String,

        photoUrl: String

    ) {

        val bookingRef =

            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("bookings")
                .child(bookingId)

        bookingRef.child(
            "endReading"
        ).setValue(endReading)

        bookingRef.child(
            "endPhotoUrl"
        ).setValue(photoUrl)

        bookingRef.child(
            "status"
        ).setValue(
            "PENDING_BILLING"
        )

        Toast.makeText(
            this,
            "Vehicle Returned Successfully",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }
}