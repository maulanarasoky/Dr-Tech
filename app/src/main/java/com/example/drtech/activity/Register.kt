package com.example.drtech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.example.drtech.adapter.ViewPager
import com.example.drtech.fragment.RegisterRegular
import com.example.drtech.fragment.RegisterSpecialist
import com.example.drtech.model.Users
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.btnRegister
import kotlinx.android.synthetic.main.activity_register.tabLayout
import kotlinx.android.synthetic.main.activity_register.viewPager
import kotlinx.android.synthetic.main.fragment_register_regular.email
import kotlinx.android.synthetic.main.fragment_register_regular.name
import kotlinx.android.synthetic.main.fragment_register_regular.password
import kotlinx.android.synthetic.main.fragment_register_specialist.*
import org.jetbrains.anko.startActivity

class Register : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val viewPagerAdapter =
            ViewPager(
                supportFragmentManager
            )
        viewPagerAdapter.addFragment(RegisterRegular(), "Regular")
        viewPagerAdapter.addFragment(RegisterSpecialist(), "Specialist")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        btnRegister.setOnClickListener {
            if(viewPager.currentItem == 0){
                registerRegular(email.text.toString(), password.text.toString())
            }else{
                registerSpecialist(emailSpecialist.text.toString(), passwordSpecialist.text.toString())
            }
        }

        haveAccount.setOnClickListener {
            startActivity<Login>()
            this.finish()
        }
    }

    private fun registerRegular(email: String, password: String) {
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
                            addRegularAccount(auth.currentUser?.uid.toString())
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

    private fun registerSpecialist(email: String, password: String) {
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
                            addSpecialistAccount(auth.currentUser?.uid.toString())
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

    private fun addRegularAccount(idUser: String?){
        val name = name.text.toString().substring(0, 1).toUpperCase() + name.text.toString().substring(1)
        val data = Users(idUser, name, "Regular", "-", null,  "Verifikasi", 0)
        database.child("Users").child("Regular").child(idUser.toString()).setValue(data)
    }

    private fun addSpecialistAccount(idUser: String?){
        val listSkill: MutableList<String> = mutableListOf()
        for(i in 0 until chipGroupSkill.childCount){
            val chip = chipGroupSkill.getChildAt(i) as Chip
            listSkill.add(chip.text.toString())
        }
        val name = nameSpecialist.text.toString().substring(0, 1).toUpperCase() + nameSpecialist.text.toString().substring(1)
        val data = Users(idUser, name, "Specialist", businessName.text.toString(), listSkill,  "Belum Terverifikasi", 0)
        database.child("Users").child("Specialist").child(idUser.toString()).setValue(data)
    }
}
