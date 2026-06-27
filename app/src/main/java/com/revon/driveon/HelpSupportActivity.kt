package com.revon.driveon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class HelpSupportActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var officialContact = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)

        val txtPhone = findViewById<TextView>(R.id.txtPhone)
        val txtPhonetwo = findViewById<TextView>(R.id.txtPhonetwo)
        val txtEmail = findViewById<TextView>(R.id.txtEmail)

        val btnCallSupport = findViewById<Button>(R.id.btnCallSupport)
        val btnWhatsappSupport = findViewById<Button>(R.id.btnWhatsappSupport)

        database = FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("helpandsupport")

        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val officialContact =
                    snapshot.child("officialcontact")
                        .getValue()
                        ?.toString() ?: ""

                val supportContact =
                    snapshot.child("supportcontact")
                        .getValue()
                        ?.toString() ?: ""

                val email =
                    snapshot.child("email")
                        .getValue()
                        ?.toString() ?: ""

                txtPhone.text = officialContact
                txtPhonetwo.text = supportContact
                txtEmail.text = email

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        btnCallSupport.setOnClickListener {

            startActivity(
                Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:$officialContact")
                )
            )
        }

        btnWhatsappSupport.setOnClickListener {

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://wa.me/$officialContact")
                )
            )
        }
    }
    private fun loadSupportDetails(): Map<String, String> {

        val map = mutableMapOf<String, String>()

        assets.open("support.txt").bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val parts = line.split("=")
                if (parts.size == 2) {
                    map[parts[0].trim()] = parts[1].trim()
                }
            }
        }

        return map
    }
}