package com.example.drtech.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ImageSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.example.drtech.model.Forum
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_forum.*
import java.lang.StringBuilder


/**
 * A simple [Fragment] subclass.
 */
class AddForum : Fragment() {

    lateinit var database: DatabaseReference

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
        forumTags.setOnKeyListener(object : View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if(event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    if(forumTags.text.trim().isNotEmpty()){
                        chip()
                    }
                    forumTags.setText("")
                    return true
                }
                return false
            }
        })
    }

    private fun chip(){
        val chipValue = forumTags.text.toString().replace(" ", "")
        for(i in 0 until chipGroup.childCount){
            val data = chipGroup.getChildAt(i) as Chip
            if(data.text.toString() == chipValue){
                return
            }
        }

        val chip = Chip(context)
        chip.text = chipValue
        chip.isCloseIconVisible = true
        chip.isCheckable = false
        chip.isClickable = false
        chip.chipBackgroundColor = resources.getColorStateList(R.color.twitterColour)
        chip.setTextColor(resources.getColor(android.R.color.white))
        chip.closeIconTint = resources.getColorStateList(android.R.color.white)
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(it)
        }
        chipGroup.addView(chip)
        rowChip.visibility = View.VISIBLE
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

    private fun addForum(){
        val id = database.push().key
        var category = ""
        when {
            laptopState -> {
                category = "Laptop"
            }
            phoneState -> {
                category = "Hp"
            }
            computerState -> {
                category = "Komputer"
            }
        }
        val title = forumTitle.text.toString().substring(0, 1).toUpperCase() + forumTitle.text.toString().substring(1)
        val description = forumDescription.text.toString().substring(0, 1).toUpperCase() + forumDescription.text.toString().substring(1)
        val builder = StringBuilder()
        for(i in 0 until chipGroup.childCount){
            val chip = chipGroup.getChildAt(i) as Chip
            builder.append(chip.text.toString().substring(0, 1).toUpperCase() + chip.text.toString().substring(1))
            if(i != chipGroup.childCount - 1){
                builder.append(" ")
            }
        }
        var tag = builder.toString().replace(" ", ", ")
        if(tag.isEmpty()){
            tag = "-"
        }
        val data = Forum(id, title, description, category, tag, "0", auth.currentUser?.uid.toString())
        database.child("Forums").child(id.toString()).setValue(data)
        clear()
        chipGroup.removeAllViews()
        rowChip.visibility = View.GONE
        showAlert("Forum berhasil dibuat")
    }

    private fun clear(){
        forumTitle.setText("")
        forumDescription.setText("")
        forumTags.setText("")
    }

    private fun showAlert(title: String){
        val dialog = SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
        dialog.setCustomImage(R.drawable.ic_success_alert)
        dialog.titleText = title
        dialog.setCancelable(false)
        dialog.show()
    }
}
