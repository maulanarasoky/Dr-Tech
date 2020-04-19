package com.example.drtech.activity

import android.content.Intent
import android.os.Bundle
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
            dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)

            dialog.setCancelable(false)
            if(viewPager.currentItem == 0){
                loginRegular(email.text.toString(), password.text.toString())
            }else{
                loginSpecialist(emailSpecialist.text.toString(), passwordSpecialist.text.toString())
            }
            dialog.show()
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
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { check ->
            if (check.isSuccessful) {
                if (auth.currentUser?.isEmailVerified!!) {
                    check(auth.currentUser?.uid.toString(), "Regular", "Specialist")
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
    }

    private fun loginSpecialist(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { check ->
            if (check.isSuccessful) {
                if (auth.currentUser?.isEmailVerified!!) {
                    check(auth.currentUser?.uid.toString(), "Specialist", "Regular")
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
    }

    private fun check(userId: String, type: String, account: String){
        database.child("Users").child(type).child(userId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val data = p0.getValue(Users::class.java)
                if(data != null){
                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    dialog.titleText = "Login Berhasil"
                    dialog.setConfirmClickListener {
                        dialog.dismissWithAnimation()
                        startActivity<MainActivity>()
                        finish()
                    }
                }else{
                    dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                    dialog.titleText = "Email terdaftar di akun $account"
                    auth.signOut()
                }
            }

        })
    }
}
