package com.example.drtech.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import com.example.drtech.R
import com.example.drtech.fragment.AddForum
import com.example.drtech.fragment.Home
import com.example.drtech.fragment.Profile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val checkLogin = 100
        var homeState = false
        var addState = false
        var profileState = false
    }

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        checkColor(home)
        loadHomeFragment()
        homeState = true

        home.setOnClickListener {
            checkColor(home)
            loadHomeFragment()
            homeState = true
        }

        addForum.setOnClickListener {
            if(auth.currentUser == null){
                val intent = Intent(this, Login::class.java)
                startActivityForResult(intent, checkLogin)
                homeState = true
            }else{
                checkColor(addForum)
                loadAddForumFragment()
                addState = true
            }
        }

        profile.setOnClickListener {
            if(auth.currentUser == null){
                val intent = Intent(this, Login::class.java)
                startActivityForResult(intent, checkLogin)
                homeState = true
            }else{
                checkColor(profile)
                loadProfileFragment()
                profileState = true
            }
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

        DrawableCompat.setTint(DrawableCompat.wrap(view.drawable), resources.getColor(R.color.twitterColour))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("REQUEST CODE", requestCode.toString())
        Log.d("RESULT CODE", resultCode.toString())
        if (requestCode == checkLogin){
            checkColor(home)
            loadHomeFragment()
            homeState = true
        }
    }
}
