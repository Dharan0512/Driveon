package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class RejectedUsersActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var etSearchRejected: EditText

    private lateinit var database: DatabaseReference

    private val usersList = ArrayList<String>()
    private val filteredList = ArrayList<String>()

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_rejected_users
        )

        listView =
            findViewById(R.id.rejectedUsersList)

        etSearchRejected =
            findViewById(R.id.etSearchRejected)

        database =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("userprofiles")

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            filteredList
        )

        listView.adapter = adapter

        loadRejectedUsers()

        etSearchRejected.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    filterUsers(
                        s.toString()
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            })

        listView.setOnItemClickListener {
                _, _, position, _ ->

            val phone =
                filteredList[position]

            val intent =
                Intent(
                    this,
                    RejectedUserDetailsActivity::class.java
                )

            intent.putExtra(
                "phone",
                phone
            )

            startActivity(intent)
        }
    }

    private fun loadRejectedUsers() {

        database.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    usersList.clear()

                    for(user in snapshot.children) {

                        val status =
                            user.child("kycStatus")
                                .getValue(String::class.java)

                        if(status == "rejected") {

                            usersList.add(
                                user.key ?: ""
                            )
                        }
                    }

                    filterUsers("")
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {}
            })
    }

    private fun filterUsers(
        query: String
    ) {

        filteredList.clear()

        for(phone in usersList) {

            if(phone.contains(
                    query,
                    true
                )) {

                filteredList.add(phone)
            }
        }

        adapter.notifyDataSetChanged()
    }
}