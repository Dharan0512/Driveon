package com.revon.driveon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import com.google.firebase.storage.FirebaseStorage
import com.google.android.material.card.MaterialCardView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class PostsActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var layoutPlaceholder: LinearLayout

    private lateinit var btnSelectImage: MaterialCardView

    private var selectedImageUri: Uri? = null

    private lateinit var btnPublish: Button

    private lateinit var postsListView: ListView

    private lateinit var imgPreview: ImageView

    private lateinit var database: DatabaseReference

    private lateinit var adapter: ArrayAdapter<String>

    private val postsList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_posts)

        layoutPlaceholder =
            findViewById(R.id.layoutPlaceholder)

        btnSelectImage =
            findViewById(R.id.btnSelectImage)

        imgPreview = findViewById(R.id.imgPreview)

        btnSelectImage.setOnClickListener {

            val intent = Intent()

            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(
                intent,
                100
            )
        }

        etTitle =
            findViewById(R.id.etTitle)

        etDescription =
            findViewById(R.id.etDescription)

        btnPublish =
            findViewById(R.id.btnPublish)

        postsListView =
            findViewById(R.id.postsListView)

        database =
            FirebaseDatabase.getInstance()
                .getReference("Driveon")
                .child("homepagePosts")

        adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                postsList
            )

        postsListView.adapter = adapter

        btnPublish.setOnClickListener {

            publishPost()
        }

        loadPosts()

        postsListView.setOnItemLongClickListener {

                _, _, position, _ ->

            deletePost(position)

            true
        }
    }

    private fun publishPost() {

        val title =
            etTitle.text.toString().trim()

        val description =
            etDescription.text.toString().trim()

        if(title.isEmpty()) {

            Toast.makeText(
                this,
                "Enter title",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if(selectedImageUri == null) {

            Toast.makeText(
                this,
                "Select image",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val postId =
            database.push().key ?: return

        val storageRef =
            FirebaseStorage.getInstance()
                .reference
                .child("homepagePosts/$postId.jpg")

        storageRef.putFile(selectedImageUri!!)
            .continueWithTask {

                storageRef.downloadUrl

            }
            .addOnSuccessListener { uri ->

                val postData =
                    HashMap<String, Any>()

                postData["title"] =
                    title

                postData["description"] =
                    description

                postData["imageUrl"] =
                    uri.toString()

                postData["timestamp"] =
                    System.currentTimeMillis()

                database.child(postId)
                    .setValue(postData)

                Toast.makeText(
                    this,
                    "Post Published",
                    Toast.LENGTH_SHORT
                ).show()

                etTitle.setText("")
                etDescription.setText("")
            }
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

                        postsList.add(title)
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {
                }
            })
    }

    private fun deletePost(
        position: Int
    ) {

        database.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    var index = 0

                    for(post in snapshot.children) {

                        if(index == position) {

                            post.ref.removeValue()

                            Toast.makeText(
                                this@PostsActivity,
                                "Post Deleted",
                                Toast.LENGTH_SHORT
                            ).show()

                            break
                        }

                        index++
                    }
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {
                }
            })
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == 100 &&
            resultCode == RESULT_OK &&
            data != null
        ) {

            selectedImageUri = data.data

            layoutPlaceholder.visibility = LinearLayout.GONE

            imgPreview.visibility = ImageView.VISIBLE

            imgPreview.setImageURI(selectedImageUri)

            Toast.makeText(
                this,
                "Image Selected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}