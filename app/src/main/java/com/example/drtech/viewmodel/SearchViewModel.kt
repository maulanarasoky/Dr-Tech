package com.example.drtech.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drtech.model.Forum
import com.google.firebase.database.*

class SearchViewModel: ViewModel() {

    val forumLiveData = MutableLiveData<MutableList<Forum>>()
    val listForums: MutableList<Forum> = mutableListOf()

    lateinit var database: DatabaseReference

    fun searchForum(searchName: String){
        database = FirebaseDatabase.getInstance().reference
        database.child("Forums").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                showData(p0, searchName)
            }

        })
    }

    private fun showData(dataSnapshot: DataSnapshot, searchName: String){
        listForums.clear()
        var check = false
        for(data in dataSnapshot.children){
            val post = data.getValue(Forum::class.java)
            val tagsList: MutableList<String> = mutableListOf()
            val hardwareList: MutableList<String> = mutableListOf()
            for(tag in dataSnapshot.child(post?.id.toString()).child("tags").children){
                tagsList.add(tag.value.toString())
            }
            for(hardware in dataSnapshot.child(post?.id.toString()).child("hardware").children){
                hardwareList.add(hardware.value.toString())
            }

            for(i in 0 until tagsList.size){
                if(tagsList[i] == searchName){
                    check = true
                    break
                }
            }

            if(check == false){
                for(i in 0 until hardwareList.size){
                    if(hardwareList[i] == searchName){
                        check = true
                        break
                    }
                }
            }

            if(check == true){
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
                check = false
            }
        }
        forumLiveData.postValue(listForums)
    }

    fun getForums(): LiveData<MutableList<Forum>>{
        return forumLiveData
    }
}