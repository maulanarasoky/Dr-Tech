package com.example.drtech.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drtech.model.Comment
import com.google.firebase.database.*

class CommentViewModel : ViewModel() {

    val commentLiveData = MutableLiveData<MutableList<Comment>>()
    val listComment: MutableList<Comment> = mutableListOf()

    lateinit var database: DatabaseReference

    fun showComments(forumId: String) {
        database = FirebaseDatabase.getInstance().reference
        database.child("Comments").orderByChild("forumId").equalTo(forumId)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    showData(p0)
                }

            })
    }

    fun showData(dataSnapshot: DataSnapshot) {
        listComment.clear()
        for (data in dataSnapshot.children) {
            val post = data.getValue(Comment::class.java)
            val comment = Comment(
                dataSnapshot.child(post?.id.toString()).child("id").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("name").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("comment").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("forumId").value.toString()
            )
            listComment.add(comment)
        }
        commentLiveData.postValue(listComment)
    }

    fun getComments(): LiveData<MutableList<Comment>> {
        return commentLiveData
    }

}