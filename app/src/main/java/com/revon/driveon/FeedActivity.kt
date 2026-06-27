package com.revon.driveon

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class FeedActivity : AppCompatActivity() {

    private lateinit var feedListView: ListView

    private lateinit var database: DatabaseReference

    private val postsList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_feed)

        feedListView =
            findViewById(R.id.feedListView)

        database =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("posts")

        loadPosts()
    }

    private fun loadPosts() {

        database.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    postsList.clear()

                    for(post in snapshot.children) {

                        val title =
                            post.child("title")
                                .getValue(String::class.java)
                                ?: ""

                        val description =
                            post.child("description")
                                .getValue(String::class.java)
                                ?: ""

                        postsList.add(
                            "$title\n\n$description"
                        )
                    }

                    feedListView.adapter =
                        ArrayAdapter(
                            this@FeedActivity,
                            android.R.layout.simple_list_item_1,
                            postsList
                        )
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {
                }
            })
    }
}