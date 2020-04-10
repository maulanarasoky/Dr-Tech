package com.example.drtech.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog

import com.example.drtech.R
import com.example.drtech.activity.Login
import com.example.drtech.activity.MainActivity
import com.example.drtech.model.Forum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_add_forum.*

/**
 * A simple [Fragment] subclass.
 */
class AddForum : Fragment() {

    lateinit var database: DatabaseReference
    var count = 0

    var laptopState = false
    var phoneState = false
    var computerState = false

    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_forum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().reference

        check(laptop)
        laptopState = true

        countChildren()

        laptop.setOnClickListener {
            check(laptop)
            laptopState = true
        }

        smartphone.setOnClickListener {
            check(smartphone)
            phoneState = true
        }

        computer.setOnClickListener {
            check(computer)
            computerState = true
        }

        submit.setOnClickListener {
            if(TextUtils.isEmpty(forumTitle.text.toString())){
                forumTitle.error = "Judul tidak boleh kosong"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(forumDescription.text.toString())){
                forumDescription.error = "Deskripsi tidak boleh kosong"
                return@setOnClickListener
            }
            addForum()
        }

        clear.setOnClickListener {
            clear()
        }
    }

    private fun check(view: LinearLayout){
        if(laptopState == true){
            laptop.background = resources.getDrawable(R.drawable.background_forum_specialist)
            laptopTitle.setTextColor(resources.getColor(R.color.twitterColour))
            laptopState = false
        }

        if(phoneState == true){
            smartphone.background = resources.getDrawable(R.drawable.background_forum_specialist)
            smartphoneTitle.setTextColor(resources.getColor(R.color.twitterColour))
            phoneState = false
        }

        if(computerState == true){
            computer.background = resources.getDrawable(R.drawable.background_forum_specialist)
            computerTitle.setTextColor(resources.getColor(R.color.twitterColour))
            computerState = false
        }

        var title: TextView = laptopTitle

        if(view.id == R.id.laptop){
            title = laptopTitle
        }else if(view.id == R.id.smartphone){
            title = smartphoneTitle
        }else if(view.id == R.id.computer){
            title = computerTitle
        }

        view.background = resources.getDrawable(R.drawable.background_button_login_register)
        title.setTextColor(resources.getColor(android.R.color.white))
    }

    private fun countChildren(){
        database.child("Forums").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                count = p0.childrenCount.toInt()
            }

        })
    }

    private fun addForum(){
        val id = database.push().key
        var category = ""
        if(laptopState == true){
            category = "Laptop"
        }else if(phoneState == true){
            category = "Hp"
        }else if(computerState == true){
            category = "Komputer"
        }
        val data = Forum(id, forumTitle.text.toString(), forumDescription.text.toString(), category, forumTags.text.toString(), "0")
        database.child("Forums").child(id.toString()).setValue(data)
        count++
        clear()
        showAlert("Forum berhasil dibuat")
    }

    private fun clear(){
        forumTitle.setText("")
        forumDescription.setText("")
    }

    private fun showAlert(title: String){
        val dialog = SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
        dialog.setCustomImage(R.drawable.ic_success_alert)
        dialog.titleText = title
        dialog.setCancelable(false)
        dialog.show()
    }
}
