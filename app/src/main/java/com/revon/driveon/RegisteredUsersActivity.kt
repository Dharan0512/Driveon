package com.revon.driveon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class RegisteredUsersActivity : AppCompatActivity() {

    private lateinit var recyclerUsers: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnClearSearch: ImageView
    private lateinit var txtUserCount: TextView
    private lateinit var emptyState: View

    private lateinit var chipAll: TextView
    private lateinit var chipToday: TextView
    private lateinit var chipWeek: TextView
    private lateinit var chipOlder: TextView

    // Active quick-filter: ALL | TODAY | WEEK | OLDER
    private var currentFilter = "ALL"

    private lateinit var database: DatabaseReference

    private val allUsers =
        ArrayList<Pair<Long, RegisteredUser>>()

    private val groups =
        ArrayList<RegisteredUserGroup>()

    private lateinit var adapter: RegisteredUsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_registered_users)

        recyclerUsers =
            findViewById(R.id.recyclerUsers)

        etSearch =
            findViewById(R.id.etSearchUser)

        btnClearSearch =
            findViewById(R.id.btnClearSearch)

        txtUserCount =
            findViewById(R.id.txtUserCount)

        emptyState =
            findViewById(R.id.emptyState)

        chipAll = findViewById(R.id.chipAll)
        chipToday = findViewById(R.id.chipToday)
        chipWeek = findViewById(R.id.chipWeek)
        chipOlder = findViewById(R.id.chipOlder)

        findViewById<ImageView>(R.id.btnBack)
            .setOnClickListener { finish() }

        adapter = RegisteredUsersAdapter(
            groups,
            onUserClick = { user ->

                val intent = Intent(
                    this,
                    RegisteredUserDetailsActivity::class.java
                )

                intent.putExtra("phone", user.phone)

                startActivity(intent)
            },
            onUserCall = { user ->

                if (user.phone.isNotBlank()) {

                    startActivity(
                        Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:" + user.phone)
                        )
                    )
                }
            }
        )

        recyclerUsers.layoutManager =
            LinearLayoutManager(this)

        recyclerUsers.adapter = adapter

        setupFilterChips()

        btnClearSearch.setOnClickListener {
            etSearch.setText("")
        }

        database =
            FirebaseDatabase
                .getInstance()
                .getReference("Driveon")
                .child("userprofiles")

        loadUsers()

        etSearch.addTextChangedListener(

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

                    btnClearSearch.visibility =
                        if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                    filterUsers(

                        s.toString()

                    )

                }

                override fun afterTextChanged(

                    s: Editable?

                ) {
                }

            }

        )

    }

    private fun setupFilterChips() {

        val chips = listOf(
            chipAll to "ALL",
            chipToday to "TODAY",
            chipWeek to "WEEK",
            chipOlder to "OLDER"
        )

        for ((chip, value) in chips) {

            chip.setOnClickListener {

                if (currentFilter == value) return@setOnClickListener

                currentFilter = value
                updateChipSelection()
                filterUsers(etSearch.text.toString())
            }
        }

        updateChipSelection()
    }

    private fun updateChipSelection() {

        chipAll.isSelected = currentFilter == "ALL"
        chipToday.isSelected = currentFilter == "TODAY"
        chipWeek.isSelected = currentFilter == "WEEK"
        chipOlder.isSelected = currentFilter == "OLDER"
    }

    /**
     * Maps a date group label to the active quick-filter.
     */
    private fun passesFilter(label: String): Boolean {

        return when (currentFilter) {

            "TODAY" -> label == "TODAY"

            "WEEK" -> label == "TODAY" ||
                label == "YESTERDAY" ||
                label == "THIS WEEK"

            "OLDER" -> label != "TODAY" &&
                label != "YESTERDAY" &&
                label != "THIS WEEK"

            else -> true
        }
    }

    private fun loadUsers() {

        database.addValueEventListener(

            object : ValueEventListener {

                override fun onDataChange(

                    snapshot: DataSnapshot

                ) {

                    allUsers.clear()

                    for (user in snapshot.children) {

                        val status =

                            user.child("kycStatus")

                                .getValue(String::class.java)

                        if (status != "approved")

                            continue

                        val phone =

                            user.key ?: ""

                        val name =

                            user.child("name")

                                .getValue(String::class.java)

                                ?: "Unknown"

                        val created =

                            user.child("created_time")

                                .getValue(Long::class.java)

                                ?: 0L

                        val registeredUser =

                            RegisteredUser(

                                phone = phone,

                                name = name,

                                status = "Approved"

                            )

                        allUsers.add(

                            Pair(

                                created,

                                registeredUser

                            )

                        )

                    }

                    allUsers.sortByDescending {

                        it.first

                    }

                    txtUserCount.text =

                        allUsers.size.toString()

                    filterUsers("")

                }

                override fun onCancelled(

                    error: DatabaseError

                ) {

                }

            }

        )

    }

    private fun filterUsers(query: String) {

        val groupedMap =
            LinkedHashMap<String, MutableList<RegisteredUser>>()

        val groupedTime =
            LinkedHashMap<String, Long>()

        for ((time, user) in allUsers) {

            val matchesQuery =
                user.name.contains(query, true) ||
                user.phone.contains(query, true)

            if (!matchesQuery) continue

            val label = getGroupLabel(time)

            if (!passesFilter(label)) continue

            if (!groupedMap.containsKey(label)) {

                groupedMap[label] = mutableListOf()

                groupedTime[label] = time
            }

            groupedMap[label]!!.add(user)
        }

            val newGroups =
            ArrayList<RegisteredUserGroup>()

        // When searching or filtering, auto-expand so results are visible.
        val forceExpand = query.isNotBlank() || currentFilter != "ALL"

        var index = 0

        for ((label, users) in groupedMap) {

            newGroups.add(

                RegisteredUserGroup(

                    title = label,

                    subtitle = getSubtitle(label, groupedTime[label] ?: 0L),

                    count = users.size,

                    expanded = forceExpand || label == "TODAY" || index == 0,

                    users = users

                )

            )

            index++
        }

        adapter.updateData(newGroups)

        val isEmpty = newGroups.isEmpty()
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerUsers.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun getGroupLabel(timestamp: Long): String {

        val calendar = java.util.Calendar.getInstance()

        val userCalendar = java.util.Calendar.getInstance()
        userCalendar.timeInMillis = timestamp

        // TODAY

        if (calendar.get(java.util.Calendar.YEAR) ==
            userCalendar.get(java.util.Calendar.YEAR) &&
            calendar.get(java.util.Calendar.DAY_OF_YEAR) ==
            userCalendar.get(java.util.Calendar.DAY_OF_YEAR)
        ) {
            return "TODAY"
        }

        // YESTERDAY

        calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)

        if (calendar.get(java.util.Calendar.YEAR) ==
            userCalendar.get(java.util.Calendar.YEAR) &&
            calendar.get(java.util.Calendar.DAY_OF_YEAR) ==
            userCalendar.get(java.util.Calendar.DAY_OF_YEAR)
        ) {
            return "YESTERDAY"
        }

        // Reset

        calendar.timeInMillis = System.currentTimeMillis()

        // THIS WEEK

        val week = calendar.get(java.util.Calendar.WEEK_OF_YEAR)
        val year = calendar.get(java.util.Calendar.YEAR)

        if (week == userCalendar.get(java.util.Calendar.WEEK_OF_YEAR)
            && year == userCalendar.get(java.util.Calendar.YEAR)
        ) {
            return "THIS WEEK"
        }

        // EARLIER THIS MONTH

        if (calendar.get(java.util.Calendar.MONTH) ==
            userCalendar.get(java.util.Calendar.MONTH)
            &&
            calendar.get(java.util.Calendar.YEAR) ==
            userCalendar.get(java.util.Calendar.YEAR)
        ) {
            return "EARLIER THIS MONTH"
        }

        // Month-Year

        return java.text.SimpleDateFormat(
            "MMMM yyyy",
            java.util.Locale.getDefault()
        ).format(java.util.Date(timestamp))
    }

    private fun getSubtitle(
        label: String,
        timestamp: Long
    ): String {

        return when (label) {

            "TODAY",
            "YESTERDAY" ->

                java.text.SimpleDateFormat(
                    "dd MMM yyyy",
                    java.util.Locale.getDefault()
                ).format(java.util.Date(timestamp))

            "THIS WEEK" ->

                "Users registered in the last 7 days"

            "EARLIER THIS MONTH" ->

                "Users registered this month"

            else ->

                "Archived users"

        }
    }

}