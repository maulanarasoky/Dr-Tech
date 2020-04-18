package com.example.drtech.viewmodel

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drtech.model.Forum
import com.example.drtech.model.Users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeViewModel: ViewModel() {

    val forumLiveData = MutableLiveData<MutableList<Forum>>()
    var listForums: MutableList<Forum> = mutableListOf()

    val totalForums = MutableLiveData<Long>()
    val totalSpecialists = MutableLiveData<Long>()
    val totalTags = MutableLiveData<Long>()

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    fun showForums(firstLetter: TextView, userName: TextView) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        try {
            database.child("Forums").orderByChild("views").limitToLast(5).addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    showData(p0)
                }

            })
            if (auth.currentUser != null) {
                database.child("Users").child("Regular").child(auth.currentUser?.uid.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val data = p0.getValue(Users::class.java)
                            val name = data?.name.toString()
                            val letter = name.toCharArray()
                            val firstName = name.split(" ").toTypedArray()
                            firstLetter.text = letter[0].toString()
                            userName.text = firstName[0]
                        }
                    })
            }else{
                firstLetter.text = "A"
                userName.text = "Anonymous"
            }
        } catch (e: FirebaseException) {
            Log.d("ERROR", e.message.toString())
        }
    }

    fun showData(dataSnapshot: DataSnapshot) {
        var count = 1
        listForums.clear()
        for (data in dataSnapshot.children.reversed()) {
            val post = data.getValue(Forum::class.java)
            val tagsList: MutableList<String> = mutableListOf()
            val hardwareList: MutableList<String> = mutableListOf()
            for(tag in dataSnapshot.child(post?.id.toString()).child("tags").children){
                tagsList.add(tag.value.toString())
            }
            for(hardware in dataSnapshot.child(post?.id.toString()).child("hardware").children){
                hardwareList.add(hardware.value.toString())
            }
            val x = Forum(
                id = dataSnapshot.child(post?.id.toString()).child("id").value.toString(),
                title = dataSnapshot.child(post?.id.toString()).child("title").value.toString(),
                description = dataSnapshot.child(post?.id.toString()).child("description").value.toString(),
                category = dataSnapshot.child(post?.id.toString()).child("category").value.toString(),
                tags = tagsList,
                hardware = hardwareList,
                views = dataSnapshot.child(post?.id.toString()).child("views").value.toString().toInt(),
                userId = dataSnapshot.child(post?.id.toString()).child("userId").value.toString()
            )
            listForums.add(x)
            count++
        }
        forumLiveData.postValue(listForums)
    }

    fun showTotal(){
        database.child("Forums").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                totalForums.postValue(p0.childrenCount)
            }

        })

        database.child("Specialists").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                totalSpecialists.postValue(p0.childrenCount)
            }

        })

        database.child("Tags").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                totalTags.postValue(p0.childrenCount)
            }

        })
    }

    fun getForums(): LiveData<MutableList<Forum>> {
        return forumLiveData
    }

    fun getTotalForums(): LiveData<Long>{
        return totalForums
    }

    fun getTotalSpecialist(): LiveData<Long>{
        return totalSpecialists
    }

    fun getTotalTags(): LiveData<Long>{
        return totalTags
    }

}