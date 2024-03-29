package com.example.drtech.activity

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drtech.R
import com.example.drtech.adapter.SpecialistList
import com.example.drtech.interfaces.MyAsyncCallback
import com.example.drtech.model.Users
import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_all_specialists.*
import org.jetbrains.anko.toast
import java.lang.ref.WeakReference

class AllSpecialists : AppCompatActivity(), MyAsyncCallback {

    lateinit var database: DatabaseReference
    private lateinit var adapter: SpecialistList

    var listSpecialist: MutableList<Users> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_specialists)

        database = FirebaseDatabase.getInstance().reference

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        HomeAsync(this).execute()

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
        specialistRecyclerView.layoutManager = layoutManager

        adapter = SpecialistList(listSpecialist)

        specialistRecyclerView.adapter = adapter

        search_bar.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    textNoData.visibility = View.GONE
                    search(search_bar.text.toString())
                    toast("Mencari").show()
                    return true
                }
                return false
            }
        })

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun showSpecialists() {
        progressBar.visibility = View.VISIBLE
        try {
            database.child("Users").child("Specialist").orderByChild("name")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        showData(p0)
                    }

                })
        } catch (e: FirebaseException) {
            Log.d("ERROR", e.message.toString())
        }
    }

    private fun showData(dataSnapshot: DataSnapshot) {
        listSpecialist.clear()
        for (data in dataSnapshot.children) {
            val post = data.getValue(Users::class.java)
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
                dataSnapshot.child(post?.id.toString()).child("phoneNumber").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("favorite").value.toString().toInt()
            )
            listSpecialist.add(x)
        }
        progressBar.visibility = View.GONE
        adapter.notifyDataSetChanged()
        if (listSpecialist.size == 0) {
            textNoData.visibility = View.VISIBLE
        }
    }

    private fun search(title: String) {
        progressBar.visibility = View.VISIBLE
        var search = title
        if (title.trim().isNotEmpty()) {
            search = title.substring(0, 1).toUpperCase() + title.substring(1)
        }
        database.child("Users").child("Specialist").orderByChild("name").startAt(search)
            .endAt(search + "\uf8ff").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                showData(p0)
            }

        })
    }

    inner class HomeAsync(listener: MyAsyncCallback) : AsyncTask<Void, Unit, Unit>() {

        private val myListener: WeakReference<MyAsyncCallback> = WeakReference(listener)

        override fun onPreExecute() {
            super.onPreExecute()
            val myListener = myListener.get()
            myListener?.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?) {
            showSpecialists()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val myListener = myListener.get()
            myListener?.onPostExecute()
        }

    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onPostExecute() {
        progressBar.visibility = View.GONE
    }
}
