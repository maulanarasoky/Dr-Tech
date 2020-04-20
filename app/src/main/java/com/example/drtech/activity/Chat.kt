package com.example.drtech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drtech.R
import com.example.drtech.adapter.ChatData
import com.example.drtech.model.Chat
import com.example.drtech.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*

class Chat : AppCompatActivity() {

    companion object{
        const val SPECIALIST_ID = "SPECIALIST_ID"
    }

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var adapter: ChatData

    private lateinit var mainViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        val receiverId: String = intent.getStringExtra(SPECIALIST_ID)

        val linearLayout = LinearLayoutManager(this)
        chatRecyclerView.layoutManager = linearLayout
        chatRecyclerView.setHasFixedSize(true)


        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ChatViewModel::class.java)
        mainViewModel.showChats(auth.currentUser?.uid.toString(), receiverId)
        mainViewModel.getDataChats().observe(this, Observer { chatList ->
            adapter = ChatData(chatList)
            chatRecyclerView.adapter = adapter
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

    private fun sendMessage(senderId: String, receiverId: String, message: String){
        val complexId1 = "$senderId - $receiverId"
        val complexId2 = "$receiverId - $senderId"
        val id = database.push().key
        val data = Chat(id, senderId, receiverId, message)

        database.child("Chats").child(id.toString()).setValue(data)

        database.child("ChatGroups").child(complexId1).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val x = p0.getValue(Chat::class.java)
                if(x != null){
                    database.child("ChatGroups").child(complexId1).child(id.toString()).setValue(id)
                }else{
                    database.child("ChatGroups").child(complexId2).child(id.toString()).setValue(id)
                }
            }

        })
    }
}
