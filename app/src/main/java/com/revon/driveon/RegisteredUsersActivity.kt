package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class RegisteredUsersActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var etSearchUser: EditText

    private lateinit var txtUserCount: TextView
    private lateinit var layoutEmpty: LinearLayout

    private lateinit var database: DatabaseReference

    private var usersList = ArrayList<Pair<Long, String>>()
    private var filteredList = ArrayList<String>()

    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_registered_users
        )

        Toast.makeText(
            this,
            "Activity Opened",
            Toast.LENGTH_LONG
        ).show()

        listView =
            findViewById(R.id.registeredUsersList)

        txtUserCount = findViewById(R.id.txtUserCount)
        layoutEmpty = findViewById(R.id.layoutEmpty)

        etSearchUser =
            findViewById(R.id.etSearchUser)

        database =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("userprofiles")

        adapter = UserAdapter(
            this,
            filteredList
        )

        listView.adapter = adapter

        loadApprovedUsers()

        etSearchUser.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

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
                ) {
                }
            })

        listView.setOnItemClickListener { _, _, position, _ ->

            val selected = filteredList[position]

            if(
                selected.startsWith("🟢") ||
                selected.startsWith("🔵") ||
                selected.startsWith("📅")
            ) {
                return@setOnItemClickListener
            }

            val phone =
                selected.substringBefore(" (")

            val intent =
                Intent(
                    this,
                    RegisteredUserDetailsActivity::class.java
                )

            intent.putExtra("phone", phone)

            startActivity(intent)
        }
    }

    private fun loadApprovedUsers() {

        Toast.makeText(
            this,
            "Loading Users",
            Toast.LENGTH_LONG
        ).show()

        database.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    Toast.makeText(
                        this@RegisteredUsersActivity,
                        "Data Found: ${snapshot.childrenCount}",
                        Toast.LENGTH_LONG
                    ).show()

                    usersList.clear()

                    for(user in snapshot.children) {

                        val status =
                            user.child("kycStatus")
                                .getValue(String::class.java)

                        if(status == "approved") {

                            val phone = user.key ?: ""

                            val name =
                                user.child("name")
                                    .getValue(String::class.java)
                                    ?: "Unknown"

                            val registeredAt =
                                user.child("created_time")
                                    .getValue(Long::class.java)
                                    ?: 0L

                            usersList.add(
                                Pair(
                                    registeredAt,
                                    "$phone ($name)"
                                )
                            )
                        }
                    }

                    usersList.sortByDescending { it.first }

                    Toast.makeText(
                        this@RegisteredUsersActivity,
                        "Approved Users: ${usersList.size}",
                        Toast.LENGTH_LONG
                    ).show()

                    filterUsers("")

                    txtUserCount.text = usersList.size.toString()

                    layoutEmpty.visibility =
                        if (usersList.isEmpty()) View.VISIBLE
                        else View.GONE

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(
                        this@RegisteredUsersActivity,
                        error.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun filterUsers(query: String) {

        filteredList.clear()

        val groupedUsers =
            LinkedHashMap<String, MutableList<String>>()

        for (user in usersList) {

            if (user.second.contains(query, true)) {

                val dateLabel = getDateLabel(user.first)

                if (!groupedUsers.containsKey(dateLabel)) {
                    groupedUsers[dateLabel] = mutableListOf()
                }

                groupedUsers[dateLabel]!!.add(user.second)
            }
        }

        for ((dateLabel, users) in groupedUsers) {

            filteredList.add("$dateLabel (${users.size} Users)")

            filteredList.addAll(users)
        }

        adapter.notifyDataSetChanged()
    }

    private fun getDateLabel(timestamp: Long): String {

        val today =
            java.text.SimpleDateFormat(
                "ddMMyyyy"
            ).format(java.util.Date())

        val yesterday =
            java.text.SimpleDateFormat(
                "ddMMyyyy"
            ).format(
                java.util.Date(
                    System.currentTimeMillis() - 86400000
                )
            )

        val userDate =
            java.text.SimpleDateFormat(
                "ddMMyyyy"
            ).format(java.util.Date(timestamp))

        return when (userDate) {

            today -> "TODAY"

            yesterday -> "YESTERDAY"

            else ->
                java.text.SimpleDateFormat(
                    "dd MMM yyyy"
                ).format(java.util.Date(timestamp))
        }
    }
}