package com.example.drtech.viewmodel

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drtech.model.Forum
import com.example.drtech.model.Users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeViewModel: ViewModel() {

    val forumLiveData = MutableLiveData<MutableList<Forum>>()
    var listForums: MutableList<Forum> = mutableListOf()

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    fun showForums(firstLetter: TextView, userName: TextView) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        try {
            database.child("Forums").orderByChild("views").limitToLast(5).addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    showData(p0)
                }

            })
            if (auth.currentUser != null) {
                database.child("Users").child("Regular").child(auth.currentUser?.uid.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val data = p0.getValue(Users::class.java)
                            val name = data?.name.toString()
                            val letter = name.toCharArray()
                            val firstName = name.split(" ").toTypedArray()
                            firstLetter.text = letter[0].toString()
                            userName.text = firstName[0]
                        }
                    })
            }else{
                firstLetter.text = "A"
                userName.text = "Anonymous"
            }
        } catch (e: FirebaseException) {
            Log.d("ERROR", e.message.toString())
        }
    }

    fun showData(dataSnapshot: DataSnapshot) {
        var count = 1
        listForums.clear()
        for (data in dataSnapshot.children.reversed()) {
            val post = data.getValue(Forum::class.java)
            val x = Forum(
                dataSnapshot.child(post?.id.toString()).child("id").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("title").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("description").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("category").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("tags").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("hardware").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("views").value.toString().toInt(),
                dataSnapshot.child(post?.id.toString()).child("userId").value.toString()
            )
            listForums.add(x)
            count++
        }
        forumLiveData.postValue(listForums)
    }

    fun getForums(): LiveData<MutableList<Forum>> {
        return forumLiveData
    }

}