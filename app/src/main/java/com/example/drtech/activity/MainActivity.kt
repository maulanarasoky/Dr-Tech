package com.example.drtech.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import com.example.drtech.R
import com.example.drtech.fragment.AddForum
import com.example.drtech.fragment.Home
import com.example.drtech.fragment.Profile
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    var homeState = false
    var addState = false
    var profileState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkColor(home)
        loadHomeFragment()
        homeState = true

        home.setOnClickListener {
            checkColor(home)
            loadHomeFragment()
            homeState = true
        }

        addForum.setOnClickListener {
            checkColor(addForum)
            loadAddForumFragment()
            addState = true
        }

        profile.setOnClickListener {
            checkColor(profile)
//            loadProfileFragment()
            startActivity<Login>()
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

    private fun loadHomeFragment(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_layout, Home(), Home::class.java.simpleName)
            .commit()
    }

    private fun loadAddForumFragment(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_layout, AddForum(), AddForum::class.java.simpleName)
            .commit()
    }

    private fun loadProfileFragment(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_layout, Profile(), Profile::class.java.simpleName)
            .commit()
    }
}
