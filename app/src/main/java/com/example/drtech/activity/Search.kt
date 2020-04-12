package com.example.drtech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.drtech.R
import com.google.android.material.chip.Chip
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_search.*

class Search : AppCompatActivity() {

    lateinit var database: DatabaseReference
    val listTag: MutableList<Chip> = mutableListOf()
    val listHardware: MutableList<Chip> = mutableListOf()

    var forumState = false
    var specialistState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        database = FirebaseDatabase.getInstance().reference

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        check(allForums)
        forumState = true

        allForums.setOnClickListener {
            check(allForums)
            forumState = true
        }

        allSpecialists.setOnClickListener {
            check(allSpecialists)
            specialistState = true
        }

        showTag()
        showHardware()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun check(view: LinearLayout){
        if(forumState == true){
            allForums.background = resources.getDrawable(R.drawable.background_forum_specialist)
            forumTitle.setTextColor(resources.getColor(R.color.twitterColour))
            forumState = false
        }

        if(specialistState == true){
            allSpecialists.background = resources.getDrawable(R.drawable.background_forum_specialist)
            specialistTitle.setTextColor(resources.getColor(R.color.twitterColour))
            specialistState = false
        }

        var title: TextView = forumTitle

        if(view.id == R.id.allForums){
            title = forumTitle
        }else if(view.id == R.id.allSpecialists){
            title = specialistTitle
        }

        view.background = resources.getDrawable(R.drawable.background_button_login_register)
        title.setTextColor(resources.getColor(android.R.color.white))
    }

    private fun inputChipTag(data: String){
        val chipValue = data.replace(" ", "")
        val chip = Chip(this)
        chip.text = chipValue
        chip.isCloseIconVisible = false
        chip.isCheckable = true
        chip.isClickable = true
        chip.chipBackgroundColor = resources.getColorStateList(R.color.twitterColour)
        chip.setTextColor(resources.getColor(android.R.color.white))
        chip.closeIconTint = resources.getColorStateList(android.R.color.white)
        chip.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(chip.isChecked){
                    if(listTag.size == 3){
                        showToast("Tag maksimal 3")
                        chip.isChecked = false
                    }else{
                        listTag.add(chip)
                        showToast("TAMBAH : " + listTag.size.toString())
                    }
                }else{
                    listTag.remove(chip)
                    showToast("KURANG : " + listTag.size.toString())
                }
            }

        })
        chipGroupTags.addView(chip)
    }

    private fun inputChipHardware(data: String){
        val chipValue = data.replace(" ", "")
        val chip = Chip(this)
        chip.text = chipValue
        chip.isCloseIconVisible = false
        chip.isCheckable = true
        chip.isClickable = true
        chip.chipBackgroundColor = resources.getColorStateList(R.color.twitterColour)
        chip.setTextColor(resources.getColor(android.R.color.white))
        chip.closeIconTint = resources.getColorStateList(android.R.color.white)
        chip.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(chip.isChecked){
                    if(listHardware.size == 3){
                        showToast("Tag maksimal 3")
                        chip.isChecked = false
                    }else{
                        listHardware.add(chip)
                        showToast("TAMBAH : " + listHardware.size.toString())
                    }
                }else{
                    listHardware.remove(chip)
                    showToast("KURANG : " + listHardware.size.toString())
                }
            }

        })
        chipGroupHardware.addView(chip)
    }

    private fun showTag(){
        database.child("TAGS").orderByChild("count").limitToLast(5).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                getTag(p0)
            }

        })
    }

    private fun showHardware(){
        database.child("HARDWARE").orderByChild("count").limitToLast(5).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                getHardware(p0)
            }

        })
    }

    private fun getTag(dataSnapshot: DataSnapshot){
        chipGroupTags.removeAllViews()
        for(data in dataSnapshot.children.reversed()){
            val tagName = data.key.toString()
            inputChipTag(tagName)
        }
    }

    private fun getHardware(dataSnapshot: DataSnapshot){
        chipGroupHardware.removeAllViews()
        for(data in dataSnapshot.children.reversed()){
            val hardwareName = data.key.toString()
            inputChipHardware(hardwareName)
        }
    }

    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
