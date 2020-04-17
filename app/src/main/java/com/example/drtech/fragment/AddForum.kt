package com.example.drtech.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.example.drtech.model.Forum
import com.example.drtech.model.Hardware
import com.example.drtech.model.Tag
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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
                        inputChip(chipGroupTags, forumTags, rowChipTags)
                    }
                    forumTags.setText("")
                    return true
                }
                return false
            }
        })

        forumHardware.setOnKeyListener(object : View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if(event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    if(forumHardware.text.trim().isNotEmpty()){
                        inputChip(chipGroupHardware, forumHardware, rowChipHardware)
                    }
                    forumHardware.setText("")
                    return true
                }
                return false
            }
        })
    }

    private fun inputChip(chipGroup: ChipGroup, editText: EditText, tableRow: TableRow){
        val chipValue = editText.text.toString().replace(" ", "")
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
        chip.chipBackgroundColor = resources.getColorStateList(R.color.purpleDark)
        chip.setTextColor(resources.getColor(android.R.color.white))
        chip.closeIconTint = resources.getColorStateList(android.R.color.white)
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(it)
        }
        chipGroup.addView(chip)
        tableRow.visibility = View.VISIBLE
    }

    private fun check(view: LinearLayout){
        if(laptopState == true){
            laptop.background = resources.getDrawable(R.drawable.background_forum_specialist)
            laptopTitle.setTextColor(resources.getColor(R.color.purpleDark))
            laptopState = false
        }

        if(phoneState == true){
            smartphone.background = resources.getDrawable(R.drawable.background_forum_specialist)
            smartphoneTitle.setTextColor(resources.getColor(R.color.purpleDark))
            phoneState = false
        }

        if(computerState == true){
            computer.background = resources.getDrawable(R.drawable.background_forum_specialist)
            computerTitle.setTextColor(resources.getColor(R.color.purpleDark))
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
        val listTag: MutableList<Chip> = mutableListOf()
        val listHardware: MutableList<Chip> = mutableListOf()
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
        val builderTag = StringBuilder()
        val builderHardware = StringBuilder()
        for(i in 0 until chipGroupTags.childCount){
            val chip = chipGroupTags.getChildAt(i) as Chip
            builderTag.append(chip.text.toString())
            if(i != chipGroupTags.childCount - 1){
                builderTag.append(" ")
            }
            listTag.add(chip)
        }
        for(i in 0 until chipGroupHardware.childCount){
            val chip = chipGroupHardware.getChildAt(i) as Chip
            builderHardware.append(chip.text.toString())
            if(i != chipGroupHardware.childCount - 1){
                builderHardware.append(" ")
            }
            listHardware.add(chip)
        }
        var tag = builderTag.toString().replace(" ", ", ")
        var hardware = builderHardware.toString().replace(" ", ", ")
        if(tag.isEmpty()){
            tag = "-"
        }
        if(hardware.isEmpty()){
            hardware = "-"
        }
        val data = Forum(id, title, description, category, tag, hardware,0, auth.currentUser?.uid.toString())
        database.child("Forums").child(id.toString()).setValue(data)
        for(i in 0 until listTag.size){
            val tagName = listTag[i].text
            checkTag(tagName.toString())
        }
        for(i in 0 until listHardware.size){
            val hardwareName = listHardware[i].text
            checkHardware(hardwareName.toString())
        }
        clear()
        chipGroupTags.removeAllViews()
        chipGroupHardware.removeAllViews()
        rowChipTags.visibility = View.GONE
        showAlert("Forum berhasil dibuat")
    }

    private fun checkTag(tagName: String){
        database.child("Tags").child(tagName).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                getDataTag(p0, tagName)
            }

        })
    }

    private fun checkHardware(hardwareName: String){
        database.child("Hardware").child(hardwareName).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                getDataHardware(p0, hardwareName)
            }

        })
    }

    private fun incrementTag(tagName: String, countTag: Int){
        val database = FirebaseDatabase.getInstance().reference.child("Tags")
        val count = countTag + 1
        database.child(tagName).child("count").setValue(count)
    }

    private fun incrementHardware(hardwareName: String, countHardware: Int){
        val database = FirebaseDatabase.getInstance().reference.child("Hardware")
        val count = countHardware + 1
        database.child(hardwareName).child("count").setValue(count)
    }

    private fun pushTag(tagName: String){
        val id = database.push().key
        val data = Tag(id, tagName, 1)
        Log.d("PUSH", tagName)
        database.child("Tags").child(tagName).setValue(data)
    }

    private fun pushHardware(hardwareName: String){
        val id = database.push().key
        val data = Hardware(id, hardwareName, 1)
        Log.d("PUSH", hardwareName)
        database.child("Hardware").child(hardwareName).setValue(data)
    }

    private fun getDataTag(dataSnapshot: DataSnapshot, tagName: String){
        if(dataSnapshot.exists()){
            val countTag = dataSnapshot.child("count").value.toString().toInt()
            Log.d("INCREMENT", tagName)
            incrementTag(tagName, countTag)
        }else{
            pushTag(tagName)
        }
    }

    private fun getDataHardware(dataSnapshot: DataSnapshot, hardwareName: String){
        if(dataSnapshot.exists()){
            val countHardware = dataSnapshot.child("count").value.toString().toInt()
            Log.d("INCREMENT", hardwareName)
            incrementHardware(hardwareName, countHardware)
        }else{
            pushHardware(hardwareName)
        }
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
