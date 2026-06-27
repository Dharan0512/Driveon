package com.revon.driveon

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class BookingDetailsActivity : AppCompatActivity() {

    lateinit var txtVehicleNumber: TextView
    lateinit var btnUploadSpeedometer: Button
    lateinit var etCount: EditText
    lateinit var btnFinish: Button
    lateinit var imgSpeedometer: ImageView

    lateinit var etCustomDays: EditText
    lateinit var etCustomPrice: EditText
    lateinit var etCustomRemarks: EditText

    lateinit var txtCustomTitle: TextView
    lateinit var packageGroup: RadioGroup

    lateinit var etStartReading: EditText

    var speedometerUri: Uri? = null

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                speedometerUri = uri

                imgSpeedometer.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details)

        packageGroup =
            findViewById(R.id.packageGroup)

        txtCustomTitle =
            findViewById(R.id.txtCustomTitle)

        etCustomDays =
            findViewById(R.id.etCustomDays)

        etCustomPrice =
            findViewById(R.id.etCustomPrice)

        etCustomRemarks =
            findViewById(R.id.etCustomRemarks)

        packageGroup.setOnCheckedChangeListener {

                _, checkedId ->

            val customSelected =
                checkedId == R.id.rbCustom

            val visibility =

                if(customSelected)
                    android.view.View.VISIBLE
                else
                    android.view.View.GONE

            txtCustomTitle.visibility =
                visibility

            etCustomDays.visibility =
                visibility

            etCustomPrice.visibility =
                visibility

            etCustomRemarks.visibility =
                visibility
        }


        etStartReading =
            findViewById(R.id.etStartReading)

        btnUploadSpeedometer =
            findViewById(R.id.btnUploadSpeedometer)

        btnUploadSpeedometer.setOnClickListener {

            imagePicker.launch("image/*")

        }

        imgSpeedometer =
            findViewById(R.id.imgSpeedometer)

        btnFinish =
            findViewById(R.id.btnFinish)

        txtVehicleNumber =
            findViewById(R.id.txtVehicleNumber)

        val vehicleNumber =
            intent.getStringExtra("vehicleNumber") ?: ""

        txtVehicleNumber.text = vehicleNumber

        btnFinish.setOnClickListener {

            checkVehicleAvailability(vehicleNumber)

        }
    }

    private fun checkVehicleAvailability(
        vehicleNumber: String
    ) {

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("bookings")
            .get()
            .addOnSuccessListener { snapshot ->

                var alreadyBooked = false

                for(item in snapshot.children){

                    val bookedVehicle =
                        item.child("vehicleNumber")
                            .getValue(String::class.java)
                            ?: ""

                    val status =
                        item.child("status")
                            .getValue(String::class.java)
                            ?: ""

                    if(
                        bookedVehicle == vehicleNumber &&
                        status == "ACTIVE"
                    ){

                        alreadyBooked = true
                        break
                    }
                }

                if(alreadyBooked){

                    Toast.makeText(
                        this,
                        "Vehicle Already Booked",
                        Toast.LENGTH_LONG
                    ).show()

                }else{

                    createBooking()
                }
            }
    }

    private fun createBooking() {

        val selectedId =
            packageGroup.checkedRadioButtonId

        var customDays = ""
        var customPrice = ""
        var customRemarks = ""





        val packageCount =
            etCount.text.toString()

        if(packageCount.isEmpty()){

            Toast.makeText(
                this,
                "Enter Package Count",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if(selectedId == -1){

            Toast.makeText(
                this,
                "Select Package",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val packageType =

            findViewById<RadioButton>(
                selectedId
            ).text.toString()

        if(packageType == "Custom") {

            customDays =
                etCustomDays.text.toString()

            customPrice =
                etCustomPrice.text.toString()

            customRemarks =
                etCustomRemarks.text.toString()

            if(
                customDays.isEmpty() ||
                customPrice.isEmpty()
            ){

                Toast.makeText(
                    this,
                    "Fill Custom Package",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }
        }

        val reading =
            etStartReading.text.toString()

        if (reading.isEmpty()) {

            Toast.makeText(
                this,
                "Enter Start Reading",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (speedometerUri == null) {

            Toast.makeText(
                this,
                "Upload Speedometer Photo",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val prefs =
            getSharedPreferences(
                "DriveOnPrefs",
                MODE_PRIVATE
            )

        val phone =
            prefs.getString("phone", "")
                ?: ""

        val bookingId =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("bookings")
                .push()
                .key ?: return

        val booking = Booking(

            bookingId = bookingId,

            userPhone = phone,

            vehicleNumber =
                txtVehicleNumber.text.toString(),

            packageType = packageType,

            packageCount = packageCount,

            customDays = customDays,

            customPrice = customPrice,

            customRemarks = customRemarks,

            startReading = reading,

            date =
                SimpleDateFormat(
                    "dd-MM-yyyy HH:mm",
                    Locale.getDefault()
                ).format(Date()),

            status = "ACTIVE"
        )

        val imageRef =

            FirebaseStorage.getInstance()
                .reference
                .child(
                    "booking_start_photos/$bookingId.jpg"
                )

        imageRef.putFile(
            speedometerUri!!
        )
            .addOnSuccessListener {

                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUrl ->

                        saveBooking(
                            bookingId,
                            phone,
                            packageType,
                            packageCount,
                            customDays,
                            customPrice,
                            customRemarks,
                            reading,
                            downloadUrl.toString()
                        )
                    }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Image Upload Failed",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun saveBooking(

        bookingId: String,

        phone: String,

        packageType: String,

        packageCount: String,

        customDays: String,

        customPrice: String,

        customRemarks: String,

        reading: String,

        photoUrl: String

    ) {

        val booking = Booking(

            bookingId = bookingId,

            userPhone = phone,

            vehicleNumber =
                txtVehicleNumber.text.toString(),

            packageType = packageType,

            packageCount = packageCount,

            customDays = customDays,

            customPrice = customPrice,

            customRemarks = customRemarks,

            startReading = reading,

            startPhotoUrl = photoUrl,

            date =
                SimpleDateFormat(
                    "dd-MM-yyyy HH:mm",
                    Locale.getDefault()
                ).format(Date()),

            status = "ACTIVE"
        )

        FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("bookings")
            .child(bookingId)
            .setValue(booking)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Booking Started",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
    }

}