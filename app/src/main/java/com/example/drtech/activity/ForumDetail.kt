package com.example.drtech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.drtech.R
import com.example.drtech.model.Forum
import com.example.drtech.model.Users
import com.google.android.material.chip.Chip
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_forrum_detail.*
import org.jetbrains.anko.startActivity

class ForumDetail : AppCompatActivity() {
    companion object{
        const val data = "FORUM"
        const val COUNT_COMMENT = 100
    }

    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forrum_detail)

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        database = FirebaseDatabase.getInstance().reference

        val parcelData: Forum? = intent.getParcelableExtra(data)

        forum_title.text = parcelData?.title
        forum_description.text = parcelData?.description

        var check = false
        database.child("Users").child("Regular").child(parcelData?.userId.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(Users::class.java)
                if(user != null){
                    forum_owner.text = "By : " + user.name
                    check = true
                }
            }

        })

        if(check == false){
            database.child("Users").child("Specialist").child(parcelData?.userId.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(Users::class.java)
                    if(user != null){
                        forum_owner.text = "By : " + user.name
                        check = true
                    }
                }

            })
        }

        for (i in 0 until parcelData?.tags!!.size){
            chip(parcelData.tags[i])
        }

        for (i in 0 until parcelData.hardware!!.size){
            chip(parcelData.hardware[i])
        }

        countComments(parcelData.id.toString())

        comments.setOnClickListener {
            val intent = Intent(this, Comments::class.java)
            intent.putExtra(Comments.FORUM_ID, parcelData.id)
            startActivityForResult(intent, COUNT_COMMENT)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun chip(tagName: String){
        val chip = Chip(this)
        chip.text = tagName
        chip.textSize = 12f
        chip.isCloseIconVisible = false
        chip.isCheckable = false
        chip.isClickable = false
        chip.chipBackgroundColor = resources.getColorStateList(android.R.color.white)
        chip.setTextColor(resources.getColor(R.color.purpleDark))
        chip.setOnClickListener {
            startActivity<Search>(
                Search.TAGS to chip.text
            )
        }
        chipGroup.addView(chip)
    }

    private fun countComments(forumId: String){
        database.child("Comments").orderByChild("forumId").equalTo(forumId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                totalComments.text = p0.childrenCount.toString()
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == COUNT_COMMENT){
            val parcelData: Forum? = intent.getParcelableExtra(Companion.data)
            countComments(parcelData?.id.toString())
        }
    }

}
