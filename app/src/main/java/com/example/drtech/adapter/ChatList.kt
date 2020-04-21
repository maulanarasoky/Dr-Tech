package com.example.drtech.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drtech.R
import com.example.drtech.activity.Chat
import com.example.drtech.model.LastChat
import com.example.drtech.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.comment_list.*
import org.jetbrains.anko.startActivity

class ChatList(
    private val items: List<LastChat>,
    private val activity: Activity,
    private val status: String
) : RecyclerView.Adapter<ChatList.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.comment_list, parent, false)
        )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], activity, status)
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindItem(items: LastChat, activity: Activity, status: String) {
            val auth: FirebaseAuth = FirebaseAuth.getInstance()

            var id = ""
            if (items.senderId.toString() == auth.currentUser?.uid) {
                showSenderName(userName, items.receiverId.toString())
                id = items.receiverId.toString()
            } else if (items.receiverId.toString() == auth.currentUser?.uid) {
                showSenderName(userName, items.senderId.toString())
                id = items.senderId.toString()
            }

            userComment.text = items.message

            itemView.setOnClickListener {
                itemView.context.startActivity<Chat>(
                    Chat.RECEIVER_ID to id,
                    Chat.SENDER_NAME to userName.text
                )
                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            }
        }

        fun showSenderName(userName: TextView, senderId: String) {
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            database.child("Users").child("Regular").child(senderId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val data = p0.getValue(Users::class.java)
                        if (data != null) {
                            userName.text = data.name.toString()
                        } else {
                            showSpecialistName(userName, senderId)
                        }
                    }
                })
        }

        fun showSpecialistName(userName: TextView, senderId: String) {
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            database.child("Users").child("Specialist").child(senderId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val data = p0.getValue(Users::class.java)
                        if (data != null) {
                            userName.text = data.name.toString()
                        }
                    }
                })
        }
    }
}