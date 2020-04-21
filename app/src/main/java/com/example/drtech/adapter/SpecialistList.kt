package com.example.drtech.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.drtech.R
import com.example.drtech.activity.SpecialistDetail
import com.example.drtech.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.specialist_list.*
import org.jetbrains.anko.startActivity

class SpecialistList(private val items: List<Users>) :
    RecyclerView.Adapter<SpecialistList.ViewHolder>() {
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
        private var status = ""
        fun bindItem(items: Users) {
            showRegularName()

            specialistName.text = items.name
            specialistFavorite.text = items.favorite.toString()

            val stringBuilder = StringBuilder()

            if (items.skills != null) {
                for (i in 0 until items.skills.size) {
                    var text = ""
                    if (i != items.skills.size - 1) {
                        text = items.skills[i] + ", "
                    } else {
                        text = items.skills[i]
                    }
                    stringBuilder.append(text)
                }
            }

            specialistSkills.text = "Keahlian : $stringBuilder"

            Glide.with(itemView.context).load(R.drawable.ic_dr_tech).into(forum_pic)

            itemView.setOnClickListener {
                if (status == "Specialist") {
                    val dialog =
                        SweetAlertDialog(itemView.context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    dialog.setCustomImage(itemView.resources.getDrawable(R.drawable.ic_dr_tech))
                    dialog.titleText = "Fitur ini tidak tersedia bagi Specialist"
                    dialog.setCancelable(false)
                    dialog.show()
                    return@setOnClickListener
                }
                itemView.context.startActivity<SpecialistDetail>(
                    SpecialistDetail.DATA to items,
                    "skills" to stringBuilder
                )
            }
        }

        fun showRegularName() {
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
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
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
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
}