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

class HomeViewModel : ViewModel() {

    val forumLiveData = MutableLiveData<MutableList<Forum>>()
    var listForums: MutableList<Forum> = mutableListOf()

    val specialistLiveData = MutableLiveData<MutableList<Users>>()
    val listSpecialists: MutableList<Users> = mutableListOf()

    val totalForums = MutableLiveData<Long>()
    val totalSpecialists = MutableLiveData<Long>()
    val totalTags = MutableLiveData<Long>()

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    fun showForums(firstLetter: TextView, userName: TextView) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        try {
            database.child("Forums").orderByChild("views").limitToLast(5)
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        getForum(p0)
                    }

                })
            if (auth.currentUser != null) {
                var check = false
                database.child("Users").child("Regular").child(auth.currentUser?.uid.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val data = p0.getValue(Users::class.java)
                            if (data != null) {
                                val name = data.name.toString()
                                val letter = name.toCharArray()
                                val firstName = name.split(" ").toTypedArray()
                                firstLetter.text = letter[0].toString()
                                userName.text = firstName[0]
                                check = true
                            }
                        }
                    })
                if (check == false) {
                    database.child("Users").child("Specialist")
                        .child(auth.currentUser?.uid.toString())
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val data = p0.getValue(Users::class.java)
                                if (data != null) {
                                    val name = data.name.toString()
                                    val letter = name.toCharArray()
                                    val firstName = name.split(" ").toTypedArray()
                                    firstLetter.text = letter[0].toString()
                                    userName.text = firstName[0]
                                    check = true
                                }
                            }
                        })
                }
            } else {
                firstLetter.text = "A"
                userName.text = "Anonymous"
            }
        } catch (e: FirebaseException) {
            Log.d("ERROR", e.message.toString())
        }
    }

    fun showSpecialist() {
        database.child("Users").child("Specialist").orderByChild("ratings").limitToLast(5)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    getSpecialist(p0)
                }

            })
    }

    fun getSpecialist(dataSnapshot: DataSnapshot) {
        listSpecialists.clear()
        for (data in dataSnapshot.children.reversed()) {
            val post = data.getValue(Users::class.java)
            if (dataSnapshot.child(post?.id.toString())
                    .child("status").value.toString() == "Terverifikasi"
            ) {
                val skillList: MutableList<String> = mutableListOf()
                for (tag in dataSnapshot.child(post?.id.toString()).child("skills").children) {
                    skillList.add(tag.value.toString())
                }
                val x = Users(
                    dataSnapshot.child(post?.id.toString()).child("id").value.toString(),
                    dataSnapshot.child(post?.id.toString()).child("name").value.toString(),
                    dataSnapshot.child(post?.id.toString()).child("type").value.toString(),
                    dataSnapshot.child(post?.id.toString()).child("business").value.toString(),
                    skillList,
                    dataSnapshot.child(post?.id.toString()).child("status").value.toString(),
                    dataSnapshot.child(post?.id.toString()).child("phoneNumber").value.toString()
                )
                listSpecialists.add(x)
            }
        }
        specialistLiveData.postValue(listSpecialists)
    }

    fun getForum(dataSnapshot: DataSnapshot) {
        listForums.clear()
        for (data in dataSnapshot.children.reversed()) {
            val post = data.getValue(Forum::class.java)
            val tagsList: MutableList<String> = mutableListOf()
            val hardwareList: MutableList<String> = mutableListOf()
            for (tag in dataSnapshot.child(post?.id.toString()).child("tags").children) {
                tagsList.add(tag.value.toString())
            }
            for (hardware in dataSnapshot.child(post?.id.toString()).child("hardware").children) {
                hardwareList.add(hardware.value.toString())
            }
            val x = Forum(
                id = dataSnapshot.child(post?.id.toString()).child("id").value.toString(),
                title = dataSnapshot.child(post?.id.toString()).child("title").value.toString(),
                description = dataSnapshot.child(post?.id.toString())
                    .child("description").value.toString(),
                category = dataSnapshot.child(post?.id.toString())
                    .child("category").value.toString(),
                tags = tagsList,
                hardware = hardwareList,
                views = dataSnapshot.child(post?.id.toString()).child("views").value.toString()
                    .toInt(),
                userId = dataSnapshot.child(post?.id.toString()).child("userId").value.toString()
            )
            listForums.add(x)
        }
        forumLiveData.postValue(listForums)
    }

    fun showTotal() {
        database.child("Forums").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                totalForums.postValue(p0.childrenCount)
            }

        })

        database.child("Users").child("Specialist")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    totalSpecialists.postValue(p0.childrenCount)
                }

            })

        database.child("Tags").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                totalTags.postValue(p0.childrenCount)
            }

        })
    }

    fun getDataForums(): LiveData<MutableList<Forum>> {
        return forumLiveData
    }

    fun getDataSpecialists(): LiveData<MutableList<Users>> {
        return specialistLiveData
    }

    fun getTotalForums(): LiveData<Long> {
        return totalForums
    }

    fun getTotalSpecialist(): LiveData<Long> {
        return totalSpecialists
    }

    fun getTotalTags(): LiveData<Long> {
        return totalTags
    }

}