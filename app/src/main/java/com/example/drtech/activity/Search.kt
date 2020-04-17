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
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_search.*

class Search : AppCompatActivity() {

    companion object{
        const val STATE_TYPE = "TYPE"
    }

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

        if(savedInstanceState != null){
            val getData = savedInstanceState.getString(STATE_TYPE)
            if(getData == "Forum"){
                check(allForums)
                forumState = true
            }else{
                check(allSpecialists)
                specialistState = true
            }
        }

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
            forumTitle.setTextColor(resources.getColor(R.color.purpleDark))
            forumState = false
        }

        if(specialistState == true){
            allSpecialists.background = resources.getDrawable(R.drawable.background_forum_specialist)
            specialistTitle.setTextColor(resources.getColor(R.color.purpleDark))
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

    private fun inputChip(data: String, chipGroup: ChipGroup, list: MutableList<Chip>){
        val chipValue = data.replace(" ", "")
        val chip = Chip(this)
        chip.text = chipValue
        chip.isCloseIconVisible = false
        chip.isCheckable = true
        chip.isClickable = true
        chip.chipBackgroundColor = resources.getColorStateList(R.color.purpleDark)
        chip.setTextColor(resources.getColor(android.R.color.white))
        chip.closeIconTint = resources.getColorStateList(android.R.color.white)
        chip.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(chip.isChecked){
                    if(list.size == 3){
                        showToast("Tag maksimal 3")
                        chip.isChecked = false
                    }else{
                        list.add(chip)
                        showToast("TAMBAH : " + list.size.toString())
                    }
                }else{
                    list.remove(chip)
                    showToast("KURANG : " + list.size.toString())
                }
            }

        })
        chipGroup.addView(chip)
    }

    private fun showTag(){
        database.child("Tags").orderByChild("count").limitToLast(5).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                getTag(p0)
            }

        })
    }

    private fun showHardware(){
        database.child("Hardware").orderByChild("count").limitToLast(5).addValueEventListener(object : ValueEventListener{
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
            inputChip(tagName, chipGroupTags, listTag)
        }
    }

    private fun getHardware(dataSnapshot: DataSnapshot){
        chipGroupHardware.removeAllViews()
        for(data in dataSnapshot.children.reversed()){
            val hardwareName = data.key.toString()
            inputChip(hardwareName, chipGroupHardware, listHardware)
        }
    }

    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if(forumState){
            outState.putString(STATE_TYPE, "Forum")
        }else{
            outState.putString(STATE_TYPE, "Specialist")
        }
        super.onSaveInstanceState(outState)
    }
}
