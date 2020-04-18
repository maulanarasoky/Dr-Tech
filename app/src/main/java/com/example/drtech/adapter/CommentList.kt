package com.example.drtech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drtech.R
import com.example.drtech.model.Comment
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.comment_list.*

class CommentList(private val items: List<Comment>) : RecyclerView.Adapter<CommentList.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.comment_list, parent, false)
        )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position])
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindItem(items: Comment) {
            userName.text = items.name
            userComment.text = items.comment
        }
    }
}