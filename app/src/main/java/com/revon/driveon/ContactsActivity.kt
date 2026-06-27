package com.revon.driveon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class ContactsActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ContactAdapter

    private lateinit var btnSave: Button

    private var systemContacts: List<ContactModel> = emptyList()

    private val loadedContacts =
        mutableListOf<ContactStatus>()

    private var isLoaded = false

    private var userPhone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        Toast.makeText(this, "Contacts Activity Opened", Toast.LENGTH_SHORT).show()

        btnSave = findViewById(R.id.btnSaveEmergencyContacts)
        recyclerView = findViewById(R.id.recyclerView)

        btnSave.setOnClickListener { saveEmergencyContacts() }

        val searchView = findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if (::adapter.isInitialized) {
                    adapter.filter(newText ?: "")
                }
                return true
            }
        })

        userPhone = intent.getStringExtra("userPhone") ?: ""

        if (userPhone.isBlank()) {
            Toast.makeText(this, "User phone missing", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                101
            )
        } else {
            startContactFlow()
        }
    }

    private fun startContactFlow() {

        if (isLoaded) return   // 🔥 prevents duplicate loading
        isLoaded = true

        systemContacts = getContacts()
        loadContactsWithStatus()
        uploadContactsToFirebase()
    }

    fun loadContactsWithStatus() {

        val contacts = getContacts()

        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("userprofiles")
            .child(userPhone)
            .child("contacts")

        dbRef.get().addOnSuccessListener { snapshot ->

            val appPhones = mutableSetOf<String>()

            for (user in snapshot.children) {

                val phone = user.child("phone").value.toString()

                appPhones.add(cleanPhone(phone))
            }

            loadedContacts.clear()

            for (contact in contacts) {

                val cleaned = cleanPhone(contact.phone)

                val isInApp = appPhones.contains(cleaned)

                loadedContacts.add(
                    ContactStatus(
                        name = contact.name,
                        phone = contact.phone,
                        isInApp = isInApp
                    )
                )
            }

            if (!::adapter.isInitialized) {
                adapter = ContactAdapter(loadedContacts)
                recyclerView.adapter = adapter
            } else {
                adapter.notifyDataSetChanged()
            }

        }
    }

    fun uploadContactsToFirebase() {

        val contacts = getContacts()

        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("userprofiles")
            .child(userPhone)
            .child("contacts")

        Toast.makeText(
            this,
            "Uploading ${contacts.size} contacts",
            Toast.LENGTH_LONG
        ).show()

        for (contact in contacts) {

            val map = HashMap<String, String>()

            map["name"] = contact.name
            map["phone"] = contact.phone

            val cleanedPhone = cleanPhone(contact.phone)

            if (cleanedPhone.length >= 10) {

                dbRef.child(cleanedPhone).setValue(map)
            }
        }
    }

    fun getContacts(): List<ContactModel> {

        val list = mutableListOf<ContactModel>()
        val seenNumbers = HashSet<String>()

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {

            val nameIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

            val phoneIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {

                val name =
                    if (nameIndex != -1) it.getString(nameIndex) else ""

                val phone =
                    if (phoneIndex != -1) it.getString(phoneIndex) else ""

                val cleaned = cleanPhone(phone)

                if (cleaned.isNotEmpty() && cleaned.length >= 10) {

                    // 🔥 REMOVE DUPLICATES HERE
                    if (!seenNumbers.contains(cleaned)) {
                        seenNumbers.add(cleaned)
                        list.add(ContactModel(name, phone))
                    }
                }
            }
        }

        android.util.Log.d("CONTACTS", "Final unique contacts = ${list.size}")

        return list
    }

    fun cleanPhone(phone: String): String {

        return phone.replace(Regex("[^0-9]"), "")
            .takeLast(10)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startContactFlow()
        } else {
            Toast.makeText(this, "Permission required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    private fun saveEmergencyContacts() {

        val selected =
            loadedContacts.filter { it.isSelected }

        if (selected.isEmpty()) {

            Toast.makeText(
                this,
                "Select at least one contact",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (selected.size > 3) {

            Toast.makeText(
                this,
                "Maximum 3 contacts allowed",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val ref = FirebaseDatabase.getInstance()
            .getReference("Driveon")
            .child("userprofiles")
            .child(userPhone)
            .child("emergencyContacts")

        ref.removeValue()

        selected.forEachIndexed { index, contact ->

            val map = HashMap<String, Any>()

            map["name"] = contact.name
            map["phone"] = contact.phone
            map["isDriveOnUser"] = contact.isInApp

            ref.child("contact${index + 1}")
                .setValue(map)
        }

        Toast.makeText(
            this,
            "Emergency Contacts Saved",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }
}