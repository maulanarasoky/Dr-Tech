package com.example.drtech.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drtech.R
import com.example.drtech.adapter.ChatList
import com.example.drtech.model.Users
import com.example.drtech.viewmodel.ChatListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * A simple [Fragment] subclass.
 */
class Chat : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var adapter: ChatList
    lateinit var database: DatabaseReference

    private lateinit var mainViewModel: ChatListViewModel

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
        database = FirebaseDatabase.getInstance().reference

        showRegularName()

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
            ChatListViewModel::class.java
        )
        mainViewModel.showRegularChat(auth.currentUser?.uid.toString())
        mainViewModel.getData().observe(this, Observer { chatList ->
            if (chatList.size > 0) {
                adapter = ChatList(chatList, activity!!, status)
                chatRecyclerView.adapter = adapter
                chatRecyclerView.visibility = View.VISIBLE
                textNoData.visibility = View.GONE
                Log.d("WEWEW", chatList.toString())
            }
        })
    }

    fun showRegularName() {
        database.child("Users").child("Regular").child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val data = p0.getValue(Users::class.java)
                    if (data == null || data.toString() == "null") {
                        showSpecialistName()
                    } else {
                        status = "Regular"
                    }
                }
            })

    }

    fun showSpecialistName() {
        database.child("Users").child("Specialist").child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val data = p0.getValue(Users::class.java)
                    if (data != null || data.toString() != "null") {
                        status = "Specialist"
                    }
                }
            })
    }

}
