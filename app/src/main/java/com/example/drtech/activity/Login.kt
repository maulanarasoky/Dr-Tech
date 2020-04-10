package com.example.drtech.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.example.drtech.model.Forum
import com.example.drtech.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.startActivity

class Login : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        btnLogin.setOnClickListener {
            login(email.text.toString(), password.text.toString())
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivityForResult(intent, MainActivity.checkLogin)
            this.finish()
        }

        forgotPass.setOnClickListener {
            startActivity<ForgotPassword>()
        }
    }

    private fun login(email: String, password: String) {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.setCancelable(false)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { check ->
            if (check.isSuccessful) {
                if (auth.currentUser?.isEmailVerified!!) {
                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    dialog.titleText = "Login Berhasil"
                    dialog.setConfirmClickListener {
                        dialog.dismissWithAnimation()
                        startActivity<MainActivity>()
                        this.finish()
                    }
                } else {
                    auth.signOut()
                    dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                    dialog.titleText = "Silahkan Lakukan Verifikasi Email Terlebih Dahulu"
                }
            } else {
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = check.exception?.message
            }
        }
        dialog.show()
    }
}
