package com.example.drtech.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drtech.R
import com.example.drtech.adapter.CommentList
import com.example.drtech.model.Comment
import com.example.drtech.model.Users
import com.example.drtech.viewmodel.CommentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_comments.*


class Comments : AppCompatActivity() {
    companion object{
        const val FORUM_ID = "FORUM_ID"
    }

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    private lateinit var mainViewModel: CommentViewModel
    lateinit var adapter: CommentList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        val layoutManager = LinearLayoutManager(this)
        commentsRecyclerView.layoutManager = layoutManager
        commentsRecyclerView.setHasFixedSize(true)
        commentsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CommentViewModel::class.java)
        intent?.getStringExtra(FORUM_ID)?.let { mainViewModel.showComments(it) }
        showLoading(true)
        mainViewModel.getComments().observe(this, Observer { commentItems ->
            adapter = CommentList(commentItems)
            Log.d("DATA", commentItems.toString())
            commentsRecyclerView.adapter = adapter
            showLoading(false)

        })

        var name = ""
        database.child("Users").child("Regular").child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(Users::class.java)
                    name = user?.name.toString()

                }
            })

        sendComment.setOnClickListener {
            sendComment(name)
            commentText.setText("")
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top)
    }

    private fun sendComment(name: String){
        val id = database.push().key
        val data = Comment(id, name, commentText.text.toString(), intent.getStringExtra(FORUM_ID))
        database.child("Comments").child(id.toString()).setValue(data)
        adapter.notifyDataSetChanged()
    }

    private fun showLoading(state: Boolean){
        if (state){
            progressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.GONE
        }
    }
}
