package com.example.drtech.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
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

        specialistRegister.setOnClickListener {
            startActivity<RegisterSpecialist>()
            this.finish()
        }

        btnRegister.setOnClickListener {
            if (TextUtils.isEmpty(name.text.toString().trim())) {
                name.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(email.text.toString().trim())) {
                email.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString().trim()).matches()) {
                email.error = "Email tidak valid"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password.text.toString().trim())) {
                password.error = "Password tidak boleh kosong"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(reTypePassword.text.toString().trim())) {
                reTypePassword.error = "Re-Type Password tidak boleh kosong"
                return@setOnClickListener
            }

            if (password.text.toString() != reTypePassword.text.toString()) {
                password.error = "Password dan Re-Type Password tidak sama"
                reTypePassword.error = "Password dan Re-Type Password tidak sama"
                return@setOnClickListener
            }


            registerRegular(email.text.toString(), password.text.toString())
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

    private fun addRegularAccount(idUser: String?) {
        val name =
            name.text.toString().substring(0, 1).toUpperCase() + name.text.toString().substring(1)
        val data = Users(idUser, name, "Regular", "-", null, "Verifikasi", "0")
        database.child("Users").child("Regular").child(idUser.toString()).setValue(data)
    }
}
