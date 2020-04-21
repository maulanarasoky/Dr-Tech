package com.example.drtech.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drtech.R
import com.example.drtech.adapter.ChatData
import com.example.drtech.model.Chat
import com.example.drtech.model.LastChat
import com.example.drtech.model.Users
import com.example.drtech.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*

class Chat : AppCompatActivity() {

    companion object {
        const val RECEIVER_ID = "RECEIVER_ID"
        const val SENDER_NAME = "SENDER_NAME"
    }

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var adapter: ChatData

    var myName = ""
    var senderName: List<String> = ArrayList()

    private lateinit var mainViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        val receiverId: String = intent.getStringExtra(RECEIVER_ID)
        val getName: String = intent.getStringExtra(SENDER_NAME)
        val sender = getName.split(" ")

        chatName.text = sender[0]

        showName()

        val linearLayout = LinearLayoutManager(this)
        chatRecyclerView.layoutManager = linearLayout
        chatRecyclerView.setHasFixedSize(true)


        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ChatViewModel::class.java)
        mainViewModel.showChats(auth.currentUser?.uid.toString(), receiverId)
        mainViewModel.getDataChats().observe(this, Observer { chatList ->
            adapter = ChatData(chatList, senderName[0])
            chatRecyclerView.adapter = adapter
            if (chatList.size > 0) {
                chatRecyclerView.smoothScrollToPosition(chatList.size - 1)
            }
            Log.d("WEWEW", chatList.toString())
        })

        sendComment.setOnClickListener {
            sendMessage(auth.currentUser?.uid.toString(), receiverId, commentText.text.toString())
            commentText.setText("")
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        val id = database.push().key
        var senderName = myName.split(" ")
        val data = Chat(id, senderId, senderName[0], receiverId, message)

        database.child("Chats").child(id.toString()).setValue(data)

        val idGroup = database.push().key
        val lastChat = LastChat(idGroup, id, senderId, receiverId, message)

        database.child("LastChat").child(senderId).child(receiverId).setValue(lastChat)
        database.child("LastChat").child(receiverId).child(senderId).setValue(lastChat)
    }

    private fun showName() {
        var check = false
        database.child("Users").child("Regular").child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val data = p0.getValue(Users::class.java)
                    if (data != null) {
                        myName = data.name.toString()
                        senderName = myName.split(" ")
                        check = true
                    }
                }
            })
        if (check == false) {
            database.child("Users").child("Specialist").child(auth.currentUser?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val data = p0.getValue(Users::class.java)
                        if (data != null) {
                            myName = data.name.toString()
                            senderName = myName.split(" ")
                            check = true
                        }
                    }
                })
        }
    }
}
