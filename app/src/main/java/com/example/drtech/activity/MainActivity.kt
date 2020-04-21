package com.example.drtech.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.drtech.R
import com.example.drtech.fragment.AddForum
import com.example.drtech.fragment.Chat
import com.example.drtech.fragment.Home
import com.example.drtech.fragment.Profile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val checkLogin = 100
    }

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadHomeFragment()
                }
                R.id.addForum -> {
                    if (auth.currentUser == null) {
                        val intent = Intent(this, Login::class.java)
                        startActivityForResult(intent, checkLogin)
                    } else {
                        loadAddForumFragment()
                    }
                }
                R.id.profile -> {
                    if (auth.currentUser == null) {
                        val intent = Intent(this, Login::class.java)
                        startActivityForResult(intent, checkLogin)
                    } else {
                        loadProfileFragment()
                    }
                }
                R.id.chat -> {
                    if (auth.currentUser == null) {
                        val intent = Intent(this, Login::class.java)
                        startActivityForResult(intent, checkLogin)
                    } else {
                        loadChatFragment()
                    }
                }
            }
            true
        }

        if (savedInstanceState == null) {
            bottom_navigation.selectedItemId = R.id.home
        }
    }

    private fun loadHomeFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_layout, Home(), Home::class.java.simpleName)
            .commit()
    }

    private fun loadAddForumFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_layout, AddForum(), AddForum::class.java.simpleName)
            .commit()
    }

    private fun loadProfileFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_layout, Profile(), Profile::class.java.simpleName)
            .commit()
    }

    private fun loadChatFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_layout, Chat(), Chat::class.java.simpleName)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("REQUEST CODE", requestCode.toString())
        Log.d("RESULT CODE", resultCode.toString())
        if (requestCode == checkLogin) {
            bottom_navigation.selectedItemId = R.id.home
        } else if (requestCode == Profile.CHANGE_NAME) {
            bottom_navigation.selectedItemId = R.id.profile
        }
    }
}
