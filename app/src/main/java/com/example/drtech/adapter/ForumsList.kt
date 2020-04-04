package com.example.drtech.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drtech.R
import com.example.drtech.model.Forum
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.forum_list.*

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
            forumViews.text = "Dilihat : " + items.views + " kali"
        }
    }
}