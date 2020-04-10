package com.example.drtech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.example.drtech.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity

class Register : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        btnRegister.setOnClickListener {
            registerAccount(email.text.toString(), password.text.toString())
        }

        haveAccount.setOnClickListener {
            startActivity<Login>()
            this.finish()
        }
    }

    private fun registerAccount(email: String, password: String) {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.setCancelable(false)
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { insert ->
            if (insert.isSuccessful) {
                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verify ->
                    if (verify.isSuccessful) {
                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        dialog.titleText = "Registrasi Berhasil"
                        dialog.contentText = "Silahkan lakukan verifikasi"
                        dialog.setConfirmClickListener {
                            dialog.dismissWithAnimation()
                            addAccountToDatabase(auth.currentUser?.uid.toString())
                            auth.signOut()
                            startActivity<Login>()
                            this.finish()
                        }
                    }
                }
            } else {
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = insert.exception?.message
            }
        }
        dialog.show()
    }

    private fun addAccountToDatabase(idUser: String?){
        val data = Users(idUser, name.text.toString(), "Regular", "-")
        database.child("Users").child(idUser.toString()).setValue(data)
    }
}
