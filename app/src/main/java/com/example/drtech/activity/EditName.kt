package com.example.drtech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_name.*

class EditName : AppCompatActivity() {

    companion object{
        const val ID_USER = "ID_USER"
        const val USER_NAME = "USER_NAME"
    }

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_name)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("Users")

        userName.setText(intent.getStringExtra(USER_NAME))

        btnChange.setOnClickListener {
            changeName()
        }
    }

    private fun changeName(){
        val name = userName.text.toString().substring(0, 1).toUpperCase() + userName.text.toString().substring(1)
        val userId: String = intent.getStringExtra(ID_USER)

        val result = database.child("Regular").child(userId).child("name").setValue(name)
        if (!result.isSuccessful){
            database.child("Specialist").child(userId).child("name").setValue(name)
        }

        val dialog = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
        dialog.setCancelable(false)
        dialog.titleText = "Nama berhasil diubah !"
        dialog.setConfirmClickListener {
            dialog.dismissWithAnimation()
            finish()
        }

        dialog.show()
    }
}
