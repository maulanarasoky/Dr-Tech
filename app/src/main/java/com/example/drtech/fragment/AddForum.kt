package com.example.drtech.fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import cn.pedant.SweetAlert.SweetAlertDialog

import com.example.drtech.R
import com.example.drtech.model.Forum
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_add_forum.*

/**
 * A simple [Fragment] subclass.
 */
class AddForum : Fragment() {

    lateinit var database: DatabaseReference
    var count = 0

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

        activity?.applicationContext?.let {
            ArrayAdapter.createFromResource(it, R.array.forumCategory, R.layout.spinner_item)
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    forumCategory.adapter = adapter
                }
        }

        countChildren()

        submit.setOnClickListener {
            if(TextUtils.isEmpty(forumTitle.text.toString())){
                forumTitle.error = "Judul tidak boleh kosong"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(forumDescription.text.toString())){
                forumDescription.error = "Deskripsi tidak boleh kosong"
                return@setOnClickListener
            }
            addForum()
        }

        clear.setOnClickListener {
            clear()
        }
    }

    private fun countChildren(){
        database.child("Forums").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                count = p0.childrenCount.toInt()
            }

        })
    }

    private fun addForum(){
        val id = database.push().key
        val data = Forum(id, forumTitle.text.toString(), forumDescription.text.toString(), forumCategory.selectedItem.toString(), forumTags.text.toString(), "0")
        database.child("Forums").child((count + 1).toString()).setValue(data)
        count++
        clear()
        showAlert("Forum berhasil dibuat")
    }

    private fun clear(){
        forumTitle.setText("")
        forumDescription.setText("")
    }

    private fun showAlert(title: String){
        val dialog = SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
        dialog.setCustomImage(R.drawable.ic_success_alert)
        dialog.titleText = title
        dialog.setCancelable(false)
        dialog.show()
    }
}
