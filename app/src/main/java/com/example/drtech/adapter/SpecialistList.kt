package com.example.drtech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drtech.R
import com.example.drtech.activity.ForumDetail
import com.example.drtech.model.Forum
import com.example.drtech.model.Users
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.specialist_list.*
import org.jetbrains.anko.startActivity
import java.lang.StringBuilder

class SpecialistList(private val items: List<Users>) : RecyclerView.Adapter<SpecialistList.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.specialist_list, parent, false)
        )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position])
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindItem(items: Users) {
            specialistName.text = items.name
            specialistFavorite.text = items.ratings.toString()

            val stringBuilder = StringBuilder()

            if(items.skills != null){
                for(i in 0 until items.skills.size){
                    var text = ""
                    if(i != items.skills.size - 1){
                        text = items.skills[i] + ", "
                    }else{
                        text = items.skills[i]
                    }
                    stringBuilder.append(text)
                }
            }

            specialistSkills.text = "Keahlian : $stringBuilder"

            Glide.with(itemView.context).load(R.drawable.ic_dr_tech).into(forum_pic)
        }
    }
}