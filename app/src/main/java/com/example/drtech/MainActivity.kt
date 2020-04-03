package com.example.drtech

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var homeState = false
    var addState = false
    var profileState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkColor(home)
        homeState = true

        home.setOnClickListener {
            checkColor(home)
            homeState = true
        }

        addForum.setOnClickListener {
            checkColor(addForum)
            addState = true
        }

        profile.setOnClickListener {
            checkColor(profile)
            profileState = true
        }
    }

    private fun checkColor(view: ImageView){
        if(homeState == true){
            DrawableCompat.setTint(DrawableCompat.wrap(home.drawable), Color.parseColor("#979797"))
            homeState = false
        }

        if(addState == true){
            DrawableCompat.setTint(DrawableCompat.wrap(addForum.drawable), Color.parseColor("#979797"))
            addState = false
        }

        if(profileState == true){
            DrawableCompat.setTint(DrawableCompat.wrap(profile.drawable), Color.parseColor("#979797"))
            profileState = false
        }

        DrawableCompat.setTint(DrawableCompat.wrap(view.drawable), Color.parseColor("#1DA1F2"))
    }
}
