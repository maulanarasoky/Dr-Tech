package com.example.drtech.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drtech.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileViewModel : ViewModel() {

    val commentLiveData = MutableLiveData<MutableList<Comment>>()
    val listComment: MutableList<Comment> = mutableListOf()

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth


    fun getComments() {
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        database.child("Comments").orderByChild("userId").equalTo(auth.currentUser?.uid.toString())
            .limitToLast(3).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                showComments(p0)
            }

        })
    }

    private fun showComments(dataSnapshot: DataSnapshot) {
        for (i in dataSnapshot.children.reversed()) {
            val data = i.getValue(Comment::class.java)
            if (data != null) {
                listComment.add(data)
            }
        }
        commentLiveData.postValue(listComment)
    }

    fun getData(): LiveData<MutableList<Comment>> {
        return commentLiveData
    }

}