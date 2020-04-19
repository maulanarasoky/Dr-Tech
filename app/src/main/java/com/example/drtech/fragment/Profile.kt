package com.example.drtech.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.pedant.SweetAlert.SweetAlertDialog

import com.example.drtech.R
import com.example.drtech.activity.Comments
import com.example.drtech.activity.EditName
import com.example.drtech.activity.Login
import com.example.drtech.activity.MainActivity
import com.example.drtech.model.Comment
import com.example.drtech.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * A simple [Fragment] subclass.
 */
class Profile : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    private val listComment: MutableList<Comment> = mutableListOf()

    var userId = ""

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

        getComments()
        getUserName()

        editName.setOnClickListener {
            startActivity<EditName>(
                EditName.USER_NAME to userName.text,
                EditName.ID_USER to userId
            )
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

    private fun getComments(){
        database.child("Comments").orderByChild("userId").equalTo(auth.currentUser?.uid.toString()).limitToLast(3).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                showComments(p0)
            }

        })
    }

    private fun showComments(dataSnapshot: DataSnapshot){
        for(i in dataSnapshot.children.reversed()){
            val data = i.getValue(Comment::class.java)
            if (data != null) {
                listComment.add(data)
            }
        }

        when(listComment.size){
            1 -> {
                firstComment.text = listComment[0].comment
                linear1.setOnClickListener {
                    startActivity<Comments>(
                        Comments.FORUM_ID to listComment[0].forumId
                    )
                    activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                }
            }
            2 -> {
                firstComment.text = listComment[0].comment
                secondComment.text = listComment[1].comment

                linear1.setOnClickListener {
                    startActivity<Comments>(
                        Comments.FORUM_ID to listComment[0].forumId
                    )
                    activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                }

                linear2.setOnClickListener {
                    startActivity<Comments>(
                        Comments.FORUM_ID to listComment[1].forumId
                    )
                    activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                }
            }
            3 -> {
                firstComment.text = listComment[0].comment
                secondComment.text = listComment[1].comment
                thirdComment.text = listComment[2].comment

                linear1.setOnClickListener {
                    startActivity<Comments>(
                        Comments.FORUM_ID to listComment[0].forumId
                    )
                    activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                }

                linear2.setOnClickListener {
                    startActivity<Comments>(
                        Comments.FORUM_ID to listComment[1].forumId
                    )
                    activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                }

                linear3.setOnClickListener {
                    startActivity<Comments>(
                        Comments.FORUM_ID to listComment[2].forumId
                    )
                    activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                }
            }
        }
    }

    private fun getUserName(){
        var check = false
        database.child("Users").child("Regular").child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val data = p0.getValue(Users::class.java)
                    if(data != null){
                        userName.text = data.name
                        type.text = data.type
                        userId = data.id.toString()
                        check = true
                    }
                }
            })
        if(check == false) {
            database.child("Users").child("Specialist").child(auth.currentUser?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val data = p0.getValue(Users::class.java)
                        if (data != null) {
                            userName.text = data.name
                            type.text = data.type
                            userId = data.id.toString()
                            check = true
                        }
                    }
                })
        }
    }
}
