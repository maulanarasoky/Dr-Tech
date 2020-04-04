package com.example.drtech.fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.pedant.SweetAlert.SweetAlertDialog

import com.example.drtech.R
import com.example.drtech.model.Forum
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_forum.*

/**
 * A simple [Fragment] subclass.
 */
class AddForum : Fragment() {

    lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_forum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().reference

        submit.setOnClickListener {
            if(TextUtils.isEmpty(forumTitle.text.toString())){
                forumTitle.error = "Judul tidak boleh kosong"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(forumDesc.text.toString())){
                forumDesc.error = "Deskripsi tidak boleh kosong"
                return@setOnClickListener
            }
            addForum()
        }

        clear.setOnClickListener {
            clear()
        }
    }

    private fun addForum(){
        val id = database.push().key
        val data = Forum(id, forumTitle.text.toString(), forumDesc.text.toString(), 0)
        database.child("Forums").child(id.toString()).setValue(data)
        clear()
        showAlert("Forum berhasil dibuat")
    }

    private fun clear(){
        forumTitle.setText("")
        forumDesc.setText("")
    }

    private fun showAlert(title: String){
        val dialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        dialog.titleText = title
        dialog.setCancelable(false)
        dialog.show()
    }
}
