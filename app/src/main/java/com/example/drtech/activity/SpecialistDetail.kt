package com.example.drtech.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.example.drtech.R
import com.example.drtech.model.Favorite
import com.example.drtech.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_specialist_detail.*
import org.jetbrains.anko.startActivity

class SpecialistDetail : AppCompatActivity() {

    companion object {
        const val DATA = "DATA"
    }

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    var favoriteCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specialist_detail)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val parcelData: Users = intent.getParcelableExtra(DATA)
        val skills = intent.getStringExtra("skills")

        if(auth.currentUser != null){
            checkFavorite(parcelData.id.toString())
            checkFavoriteCount(parcelData.id.toString())
        }else{
            favoriteBtn.visibility = View.GONE
        }

        userName.text = parcelData.name
        businessName.text = parcelData.business
        specialistSkills.text = skills

        chat.setOnClickListener {
            if (auth.currentUser != null) {
                startActivity<Chat>(
                    Chat.RECEIVER_ID to parcelData.id,
                    Chat.SENDER_NAME to parcelData.name
                )
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            } else {
                startActivity<Login>()
                finish()
            }
        }

        call.setOnClickListener {
            val uri = "tel:" + parcelData.phoneNumber
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(uri))
            startActivity(intent)
        }

        backBtn.setOnClickListener {
            finish()
        }
        favoriteBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                favoriteBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_favorite))
            }else{
                favoriteBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_unfavorite))
            }
        }
        favoriteBtn.setOnClickListener {
            if(!favoriteBtn.isSelected){
                favoriteBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_favorite))
                addFavorite(parcelData.id.toString())
                favoriteBtn.isSelected = true
            }else{
                favoriteBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_unfavorite))
                removeFavorite(parcelData.id.toString())
                favoriteBtn.isSelected = false
            }
        }
    }

    private fun addFavorite(specialistId: String){
        val id = database.push().key
        val data = Favorite(id, auth.currentUser?.uid.toString(), specialistId)
        database.root.child("Favorites").child(auth.currentUser?.uid.toString()).child(specialistId).setValue(data)
        database.root.child("Users").child("Specialist").child(specialistId).child("favorite").setValue(favoriteCount + 1)
    }

    private fun removeFavorite(specialistId: String){
        database.root.child("Users").child("Specialist").child(specialistId).child("favorite").setValue(favoriteCount - 1)
        database.root.child("Favorites").child(auth.currentUser?.uid.toString()).orderByChild(specialistId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    database.root.child("Favorites").child(auth.currentUser?.uid.toString()).child(specialistId).removeValue()
                }
            }

        })
    }

    private fun checkFavorite(specialistId: String){
        database.root.child("Favorites").child(auth.currentUser?.uid.toString()).child(specialistId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("FAVORITES", p0.toString())
                if(p0.exists()){
                    favoriteBtn.isSelected = true
                    favoriteBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_favorite))
                }else{
                    favoriteBtn.isSelected = false
                    favoriteBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_unfavorite))
                }
            }

        })
    }

    private fun checkFavoriteCount(specialistId: String){
        database.root.child("Users").child("Specialist").child(specialistId).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("USERS", p0.toString())
                if(p0.exists()){
                    val data = p0.getValue(Users::class.java)
                    favoriteCount = data?.favorite?.toString()?.toInt()!!
                }
            }

        })
    }
}
