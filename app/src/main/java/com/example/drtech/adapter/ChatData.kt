package com.example.drtech.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drtech.R
import com.example.drtech.model.Chat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.anko.find

class ChatData(private val items: List<Chat>) : RecyclerView.Adapter<ChatData.ViewHolder>() {

    private val MESSAGE_LEFT = 0
    private val MESSAGE_RIGHT = 1

    lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("TEMBELEK", viewType.toString())
        return if(viewType == MESSAGE_LEFT){
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_item_left, parent, false))
        }else{
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_item_right, parent, false))
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chat.text = items[position].message
    }

    override fun getItemViewType(position: Int): Int {
        auth = FirebaseAuth.getInstance()
        return if (items[position].senderId.equals(auth.currentUser?.uid)){
            MESSAGE_RIGHT
        }else{
            MESSAGE_LEFT
        }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        val chat: TextView = containerView.find(R.id.bubbleChat)
    }
}