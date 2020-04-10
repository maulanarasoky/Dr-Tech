package com.example.drtech.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drtech.R
import com.example.drtech.activity.Login
import com.example.drtech.model.Forum
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.forum_list.*
import org.jetbrains.anko.startActivity

class ForumsList(private val items: List<Forum>) : RecyclerView.Adapter<ForumsList.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.forum_list, parent, false)
        )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position])
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindItem(items: Forum) {
            forumTitle.text = items.title
            forumViews.text = items.views
            forumTags.text = items.tags

            var photo = 0

            if(items.category == "Laptop"){
                photo = R.drawable.ic_laptop
            }else if(items.category == "Komputer"){
                photo = R.drawable.ic_computer
            }else if(items.category == "Hp"){
                photo = R.drawable.ic_smartphone
            }

            Glide.with(itemView.context).load(photo).into(forumPic)

            itemView.setOnClickListener {
                updateViews(items.id.toString(), items.views.toString())
            }
        }

        private fun updateViews(id: String, views: String){
            val database = FirebaseDatabase.getInstance().reference.child("Forums")
            val view = views.toInt() + 1
            database.child(id).child("views").setValue(view.toString())
        }
    }
}