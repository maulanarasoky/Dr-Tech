package com.example.drtech.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drtech.model.Chat
import com.google.firebase.database.*

class ChatViewModel: ViewModel() {

    private val chatLiveData = MutableLiveData<MutableList<Chat>>()
    private val listChat: MutableList<Chat> = mutableListOf()

    lateinit var database: DatabaseReference

    fun showChats(senderId: String, receiverId: String){
        database = FirebaseDatabase.getInstance().reference
        database.child("Chats").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val data = p0.getValue(Chat::class.java)
                if(data != null){
                    getChats(p0, senderId, receiverId)
                    Log.d("SIZE DATA", p0.childrenCount.toString())
                }
            }

        })
    }

    private fun getChats(dataSnapshot: DataSnapshot, senderId: String, receiverId: String){
        listChat.clear()
        for(data in dataSnapshot.children){
            val post = data.getValue(Chat::class.java)
            val condition1 = dataSnapshot.child(post?.id.toString()).child("senderId").value.toString() == senderId && dataSnapshot.child(post?.id.toString()).child("receiverId").value.toString() == receiverId
            val condition2 = dataSnapshot.child(post?.id.toString()).child("receiverId").value.toString() == senderId && dataSnapshot.child(post?.id.toString()).child("senderId").value.toString() == receiverId
            if(condition1 || condition2){
                val x = Chat(
                    dataSnapshot.child(post?.id.toString()).child("id").value.toString(),
                    dataSnapshot.child(post?.id.toString()).child("senderId").value.toString(),
                    dataSnapshot.child(post?.id.toString()).child("receiverId").value.toString(),
                    dataSnapshot.child(post?.id.toString()).child("message").value.toString()
                )
                listChat.add(x)
            }
        }
        chatLiveData.postValue(listChat)
    }

    fun showMyChats(senderId: String){
        database = FirebaseDatabase.getInstance().reference

        database.child("Chats").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val data = p0.getValue(Chat::class.java)
                if(data != null){
                    getMyChat(p0, senderId)
                    Log.d("SIZE DATA", p0.childrenCount.toString())
                }
            }

        })
    }

    private fun getMyChat(dataSnapshot: DataSnapshot, senderId: String){
        listChat.clear()
        for(data in dataSnapshot.children){
            val post = data.getValue(Chat::class.java)
            val condition1 = dataSnapshot.child(post?.id.toString()).child("senderId").value.toString() == senderId
            val condition2 = dataSnapshot.child(post?.id.toString()).child("receiverId").value.toString() == senderId
            if(condition1 || condition2){
                val x = Chat(
                    dataSnapshot.child(post?.id.toString()).child("id").value.toString(),
                    dataSnapshot.child(post?.id.toString()).child("senderId").value.toString(),
                    dataSnapshot.child(post?.id.toString()).child("receiverId").value.toString(),
                    dataSnapshot.child(post?.id.toString()).child("message").value.toString()
                )
                listChat.add(x)
            }
        }
        chatLiveData.postValue(listChat)
    }

    fun getDataChats(): LiveData<MutableList<Chat>>{
        return chatLiveData
    }
}