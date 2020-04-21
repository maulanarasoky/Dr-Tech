package com.example.drtech.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.example.drtech.activity.*
import com.example.drtech.model.Users
import com.example.drtech.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * A simple [Fragment] subclass.
 */
class Profile : Fragment() {

    companion object {
        const val CHANGE_NAME = 101
    }

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    var userId = ""
    var typeUser = ""

    lateinit var mainViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        getUserName()

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            ProfileViewModel::class.java
        )
        mainViewModel.getComments()
        mainViewModel.getData().observe(this, Observer { commentList ->
            when (commentList.size) {
                0 -> {
                    firstComment.text = "Belum ada komentar"
                    secondComment.text = "Belum ada komentar"
                    thirdComment.text = "Belum ada komentar"
                }
                1 -> {
                    firstComment.text = commentList[0].comment
                    secondComment.text = "Belum ada komentar"
                    thirdComment.text = "Belum ada komentar"
                    linear1.setOnClickListener {
                        startActivity<Comments>(
                            Comments.FORUM_ID to commentList[0].forumId
                        )
                        activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }
                }
                2 -> {
                    firstComment.text = commentList[0].comment
                    secondComment.text = commentList[1].comment
                    thirdComment.text = "Belum ada komentar"
                    linear1.setOnClickListener {
                        startActivity<Comments>(
                            Comments.FORUM_ID to commentList[0].forumId
                        )
                        activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }

                    linear2.setOnClickListener {
                        startActivity<Comments>(
                            Comments.FORUM_ID to commentList[1].forumId
                        )
                        activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }
                }
                3 -> {
                    firstComment.text = commentList[0].comment
                    secondComment.text = commentList[1].comment
                    thirdComment.text = commentList[2].comment

                    linear1.setOnClickListener {
                        startActivity<Comments>(
                            Comments.FORUM_ID to commentList[0].forumId
                        )
                        activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }

                    linear2.setOnClickListener {
                        startActivity<Comments>(
                            Comments.FORUM_ID to commentList[1].forumId
                        )
                        activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }

                    linear3.setOnClickListener {
                        startActivity<Comments>(
                            Comments.FORUM_ID to commentList[2].forumId
                        )
                        activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }
                }
            }
        })

        showForum.setOnClickListener {
            startActivity<MyForum>()
        }

        editName.setOnClickListener {
            val intent = Intent(activity, EditName::class.java)
            intent.putExtra(EditName.USER_NAME, userName.text)
            intent.putExtra(EditName.ID_USER, userId)
            intent.putExtra(EditName.TYPE_USER, typeUser)
            activity?.startActivityForResult(intent, CHANGE_NAME)
        }

        changePassword.setOnClickListener {
            startActivity<ChangePassword>()
        }

        logOut.setOnClickListener {
            val dialog = SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Apakah Anda ingin keluar ?"
            dialog.setCancelable(false)
            dialog.showCancelButton(true)
            dialog.cancelText = "Batal"
            dialog.confirmText = "Keluar"
            dialog.setConfirmClickListener {
                dialog.dismissWithAnimation()
                auth.signOut()
                val intent = Intent(activity, Login::class.java)
                activity?.startActivityForResult(intent, MainActivity.checkLogin)
            }
            dialog.show()
        }
    }

    private fun getUserName() {
        var check = false
        database.child("Users").child("Regular").child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val data = p0.getValue(Users::class.java)
                    if (data != null) {
                        userName.text = data.name
                        type.text = data.type
                        userId = data.id.toString()
                        typeUser = data.type.toString()
                        check = true
                    }
                }
            })
        if (check == false) {
            database.child("Users").child("Specialist").child(auth.currentUser?.uid.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val data = p0.getValue(Users::class.java)
                        if (data != null) {
                            userName.text = data.name
                            type.text = data.type
                            userId = data.id.toString()
                            typeUser = data.type.toString()
                            check = true
                        }
                    }
                })
        }
    }
}
