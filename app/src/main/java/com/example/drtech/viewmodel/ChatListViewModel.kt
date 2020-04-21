package com.example.drtech.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drtech.model.LastChat
import com.google.firebase.database.*

class ChatListViewModel : ViewModel() {

    private val chatLiveData = MutableLiveData<MutableList<LastChat>>()
    private val listChat: MutableList<LastChat> = mutableListOf()

    lateinit var database: DatabaseReference

    fun showRegularChat(idUser: String) {
        database = FirebaseDatabase.getInstance().reference
        database.child("LastChat").child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val data = p0.getValue(LastChat::class.java)
                Log.d("TEMBELEK", data.toString())
                getGroupChatRegular(p0)
            }

        })
    }

    private fun getGroupChatRegular(dataSnapshot: DataSnapshot) {
        listChat.clear()
        for (data in dataSnapshot.children) {
            val post = data.getValue(LastChat::class.java)
            val x1 = LastChat(
                dataSnapshot.child(post?.receiverId.toString()).child("id").value.toString(),
                dataSnapshot.child(post?.receiverId.toString()).child("idChat").value.toString(),
                dataSnapshot.child(post?.receiverId.toString()).child("senderId").value.toString(),
                dataSnapshot.child(post?.receiverId.toString())
                    .child("receiverId").value.toString(),
                dataSnapshot.child(post?.receiverId.toString()).child("message").value.toString()
            )
            listChat.add(x1)

            var check = false
            for (i in 0 until listChat.size) {
                if (listChat[i].id == null || listChat[i].id.toString() == "null") {
                    listChat.removeAt(i)
                    check = false
                } else {
                    check = true
                }
            }

            if (check == false) {
                val x2 = LastChat(
                    dataSnapshot.child(post?.senderId.toString()).child("id").value.toString(),
                    dataSnapshot.child(post?.senderId.toString()).child("idChat").value.toString(),
                    dataSnapshot.child(post?.senderId.toString())
                        .child("senderId").value.toString(),
                    dataSnapshot.child(post?.senderId.toString())
                        .child("receiverId").value.toString(),
                    dataSnapshot.child(post?.senderId.toString()).child("message").value.toString()
                )
                listChat.add(x2)
            }
        }

        if (listChat.size > 0) {
            Log.d("DATA LAST", listChat[0].id.toString())
            if (listChat[0].id == "" || listChat[0].id == null || listChat[0].id == "null") {
                getGroupChatSpecialist(dataSnapshot)
            } else {
                chatLiveData.postValue(listChat)
            }
        }
    }

    private fun getGroupChatSpecialist(dataSnapshot: DataSnapshot) {
        Log.d("TAHAP CHAT LIST", "SPECIALIST")
        listChat.clear()
        for (data in dataSnapshot.children) {
            val post = data.getValue(LastChat::class.java)
            val x = LastChat(
                dataSnapshot.child(post?.senderId.toString()).child("id").value.toString(),
                dataSnapshot.child(post?.senderId.toString()).child("idChat").value.toString(),
                dataSnapshot.child(post?.senderId.toString()).child("senderId").value.toString(),
                dataSnapshot.child(post?.senderId.toString()).child("receiverId").value.toString(),
                dataSnapshot.child(post?.senderId.toString()).child("message").value.toString()
            )
            listChat.add(x)
        }
        chatLiveData.postValue(listChat)
    }

    fun getData(): LiveData<MutableList<LastChat>> {
        return chatLiveData
    }

}