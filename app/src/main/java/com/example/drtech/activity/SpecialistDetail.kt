package com.example.drtech.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.drtech.R
import com.example.drtech.model.Users
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_specialist_detail.*
import org.jetbrains.anko.startActivity

class SpecialistDetail : AppCompatActivity() {

    companion object{
        const val DATA = "DATA"
    }

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specialist_detail)

        auth = FirebaseAuth.getInstance()

        val parcelData: Users = intent.getParcelableExtra(DATA)
        val skills = intent.getStringExtra("skills")

        userName.text = parcelData.name
        businessName.text = parcelData.business
        specialistSkills.text = "Keahlian : $skills"

        chat.setOnClickListener {
            if(auth.currentUser != null){
                startActivity<Chat>(
                    Chat.RECEIVER_ID to parcelData.id,
                    Chat.SENDER_NAME to parcelData.name
                )
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            }else{
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
    }
}
