package com.example.drtech.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.example.drtech.adapter.ViewPager
import com.example.drtech.fragment.LoginRegular
import com.example.drtech.fragment.LoginSpecialist
import com.example.drtech.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_login_regular.email
import kotlinx.android.synthetic.main.fragment_login_regular.password
import kotlinx.android.synthetic.main.fragment_login_specialist.*
import org.jetbrains.anko.startActivity


class Login : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    private val regular: LoginRegular = LoginRegular()
    private val specialist: LoginSpecialist = LoginSpecialist()

    lateinit var dialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)

        dialog.setCancelable(false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val viewPagerAdapter =
            ViewPager(
                supportFragmentManager
            )
        viewPagerAdapter.addFragment(regular, "Regular")
        viewPagerAdapter.addFragment(specialist, "Specialist")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        btnLogin.setOnClickListener {
            if(viewPager.currentItem == 0){
                loginRegular(email.text.toString(), password.text.toString())
            }else{
                loginSpecialist(emailSpecialist.text.toString(), passwordSpecialist.text.toString())
            }
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

    private fun loginRegular(email: String, password: String) {
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

    private fun loginSpecialist(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { check ->
            if (check.isSuccessful) {
                if (auth.currentUser?.isEmailVerified!!) {
                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    dialog.titleText = "Login Berhasil"
                    dialog.setConfirmClickListener {
                        dialog.dismissWithAnimation()
                        startActivity<MainActivity>()
                        finish()
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
