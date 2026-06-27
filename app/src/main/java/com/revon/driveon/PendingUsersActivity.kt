package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class PendingUsersActivity : AppCompatActivity() {

    private lateinit var userListView: ListView
    private lateinit var etSearch: EditText

    private lateinit var usersList: ArrayList<String>

    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pending_users)

        userListView =
            findViewById(R.id.userListView)

        etSearch =
            findViewById(R.id.etSearch)

        usersList = ArrayList()

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            usersList
        )

        userListView.adapter = adapter

        database =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("userprofiles")

        loadPendingUsers()

        userListView.setOnItemClickListener { _, _, position, _ ->

            val phone =
                usersList[position]

            val intent =
                Intent(
                    this,
                    UserKycReviewActivity::class.java
                )

            intent.putExtra(
                "phone",
                phone
            )

            startActivity(intent)
        }
    }

    private fun loadPendingUsers() {

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

                        if(status == "pending") {

                            usersList.add(
                                user.key ?: ""
                            )
                        }
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {

                }
            }
        )
    }
}