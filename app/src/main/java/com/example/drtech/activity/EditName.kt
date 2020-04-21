package com.example.drtech.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.example.drtech.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_name.*

class EditName : AppCompatActivity() {

    companion object {
        const val ID_USER = "ID_USER"
        const val USER_NAME = "USER_NAME"
        const val TYPE_USER = "TYPE_USER"
    }

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_name)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("Users")

        userName.setText(intent.getStringExtra(USER_NAME))
        val before = userName.text.split(" ")

        btnChange.setOnClickListener {
            changeName(before[0])
        }
    }

    private fun changeName(beforeName: String) {
        val name = userName.text.toString().substring(0, 1).toUpperCase() + userName.text.toString()
            .substring(1)
        val userId: String = intent.getStringExtra(ID_USER)
        val typeUser: String = intent.getStringExtra(TYPE_USER)

        if (typeUser == "Regular") {
            database.child("Regular").child(userId).child("name").setValue(name)
        } else {
            database.child("Specialist").child(userId).child("name").setValue(name)
        }


        val changeName = name.split(" ")

        getSenderName(beforeName, changeName[0])

        val dialog = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
        dialog.setCancelable(false)
        dialog.titleText = "Nama berhasil diubah !"
        dialog.setConfirmClickListener {
            dialog.dismissWithAnimation()
            finish()
        }

        dialog.show()
    }

    private fun getSenderName(beforeName: String, name: String) {
        Log.d("SENDER", beforeName)
        database.root.child("Chats").orderByChild("senderName").equalTo(beforeName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    updateSenderName(p0, name)
                    Log.d("NAME", p0.toString())
                }

            })
    }

    private fun updateSenderName(dataSnapshot: DataSnapshot, name: String) {
        for (i in dataSnapshot.children) {
            val post = i.getValue(Chat::class.java)
            database.root.child("Chats").child(post?.id.toString()).child("senderName")
                .setValue(name)
        }
    }
}
