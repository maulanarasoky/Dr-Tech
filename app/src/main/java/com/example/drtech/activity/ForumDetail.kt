package com.example.drtech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.drtech.R
import com.example.drtech.model.Forum
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_forrum_detail.*
import org.jetbrains.anko.startActivity

class ForumDetail : AppCompatActivity() {
    companion object{
        const val data = "FORUM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forrum_detail)

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val parcelData: Forum? = intent.getParcelableExtra(data)

        forum_title.text = parcelData?.title
        forum_description.text = parcelData?.description

        for (i in 0 until parcelData?.tags!!.size){
            chip(parcelData.tags[i])
        }

        for (i in 0 until parcelData.hardware!!.size){
            chip(parcelData.hardware[i])
        }

        comments.setOnClickListener {
            startActivity<Comments>(
                Comments.FORUM_ID to parcelData?.id
            )
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
        chipGroup.addView(chip)
    }

}
