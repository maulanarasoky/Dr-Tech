package com.example.drtech.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.drtech.R
import com.example.drtech.adapter.ChatData
import com.example.drtech.adapter.ChatList
import com.example.drtech.model.Users
import com.example.drtech.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * A simple [Fragment] subclass.
 */
class Chat : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var adapter: ChatList

    private lateinit var mainViewModel: ChatViewModel

    var status = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        showSenderName()

        val linearLayout = LinearLayoutManager(activity)
        chatRecyclerView.layoutManager = linearLayout
        chatRecyclerView.setHasFixedSize(true)
        chatRecyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            ChatViewModel::class.java)
        mainViewModel.showMyChats(auth.currentUser?.uid.toString())
        mainViewModel.getDataChats().observe(this, Observer { chatList ->
            adapter = ChatList(chatList, activity!!, status)
            chatRecyclerView.adapter = adapter
            chatRecyclerView.visibility = View.VISIBLE
            Log.d("WEWEW", chatList.toString())
        })
    }

    fun showSenderName() {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        var check = false
        database.child("Users").child("Regular").child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val data = p0.getValue(Users::class.java)
                    if (data != null) {
                        status = "Regular"
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
                            status = "Specialist"
                            check = true
                        }
                    }
                })
        }
    }

}
