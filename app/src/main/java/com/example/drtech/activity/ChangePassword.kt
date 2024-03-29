package com.example.drtech.activity

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePassword : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        auth = FirebaseAuth.getInstance()

        btnChange.setOnClickListener {
            if (TextUtils.isEmpty(oldPassword.text.toString())) {
                oldPassword.error = "Isi dengan password yang sebelumnya"
                return@setOnClickListener
            }

            if (oldPassword.text.length < 6) {
                oldPassword.error = "Minimal 6 digit angka atau huruf"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(newPassword.text.toString())) {
                newPassword.error = "Isi dengan password yang baru"
                return@setOnClickListener
            }

            if (newPassword.text.length < 6) {
                newPassword.error = "Minimal 6 digit angka atau huruf"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(reTypeNewPassword.text.toString())) {
                reTypeNewPassword.error = "Isi dengan password yang baru"
                return@setOnClickListener
            }

            if (reTypeNewPassword.text.length < 6) {
                reTypeNewPassword.error = "Minimal 6 digit angka atau huruf"
                return@setOnClickListener
            }

            if (reTypeNewPassword.text.toString() != newPassword.text.toString()) {
                newPassword.error = "Kedua kolom tidak sama"
                reTypeNewPassword.error = "Kedua kolom tidak sama"
                return@setOnClickListener
            }

            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Apakah Anda Yakin ?"
            dialog.setCancelable(false)
            dialog.showCancelButton(true)
            dialog.cancelText = "Batal"
            dialog.confirmText = "Ubah"
            dialog.setConfirmClickListener {
                dialog.dismissWithAnimation()
                changePassword()
            }
            dialog.show()
        }
    }

    private fun changePassword() {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.setCancelable(false)
        val credential: AuthCredential = EmailAuthProvider.getCredential(
            auth.currentUser?.email.toString(),
            oldPassword.text.toString()
        )

        auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                if (newPassword.text.toString() != oldPassword.text.toString()) {
                    auth.currentUser?.updatePassword(newPassword.text.toString())
                        ?.addOnCompleteListener { change ->
                            if (change.isSuccessful) {
                                dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                dialog.titleText = "Password berhasil diubah"
                                dialog.setConfirmClickListener {
                                    dialog.dismissWithAnimation()
                                    finish()
                                }
                            }
                        }
                } else {
                    dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                    dialog.titleText = "Password baru sama seperti password lama"
                }
            } else {
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = "Password lama yang Anda masukkan salah"
            }
        }
        dialog.show()
    }
}
