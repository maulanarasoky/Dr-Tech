package com.example.drtech.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.example.drtech.model.Users
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register_specialist.*
import org.jetbrains.anko.startActivity

class RegisterSpecialist : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_specialist)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        btnRegister.setOnClickListener {
            if (TextUtils.isEmpty(name.text.toString().trim())) {
                name.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(businessName.text.toString().trim())) {
                businessName.error = "Nama Usaha tidak boleh kosong"
                return@setOnClickListener
            }

            if (chipGroupSkill.childCount == 0) {
                skill.error = "Keahlian tidak boleh kosong"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(phoneNumber.text.toString().trim())) {
                phoneNumber.error = "Nomor Telepon tidak boleh kosong"
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

            registerSpecialist(email.text.toString(), password.text.toString())
        }

        haveAccount.setOnClickListener {
            startActivity<Login>()
            this.finish()
        }

        skill.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (skill.text.trim().isNotEmpty()) {
                        inputChip(chipGroupSkill, skill)
                    }
                    skill.setText("")
                    return true
                }
                return false
            }
        })
    }

    private fun inputChip(chipGroup: ChipGroup, editText: EditText) {
        if (chipGroup.childCount == 3) {
            showAlert("Maksimal 3 keahlian")
            return
        }

        val text = editText.text.toString().substring(0, 1).toUpperCase() + editText.text.toString()
            .substring(1)
        for (i in 0 until chipGroup.childCount) {
            val data = chipGroup.getChildAt(i) as Chip
            if (data.text.toString() == text) {
                return
            }
        }

        val chip = Chip(this)
        chip.text = text
        chip.textSize = 12f
        chip.isCloseIconVisible = true
        chip.isCheckable = false
        chip.isClickable = false
        chip.chipBackgroundColor = resources.getColorStateList(R.color.purpleDark)
        chip.setTextColor(resources.getColor(android.R.color.white))
        chip.closeIconTint = resources.getColorStateList(android.R.color.white)
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(it)
        }
        chipGroup.addView(chip)
        chipGroup.visibility = View.VISIBLE
    }

    private fun showAlert(title: String) {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
        dialog.setCustomImage(R.drawable.ic_dr_tech)
        dialog.titleText = title
        dialog.setCancelable(false)
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

    private fun addSpecialistAccount(idUser: String?) {
        val listSkill: MutableList<String> = mutableListOf()
        for (i in 0 until chipGroupSkill.childCount) {
            val chip = chipGroupSkill.getChildAt(i) as Chip
            listSkill.add(chip.text.toString())
        }
        val name =
            name.text.toString().substring(0, 1).toUpperCase() + name.text.toString().substring(1)
        val data = Users(
            idUser,
            name,
            "Specialist",
            businessName.text.toString(),
            listSkill,
            "Belum Terverifikasi",
            phoneNumber.text.toString()
        )
        database.child("Users").child("Specialist").child(idUser.toString()).setValue(data)
    }
}
