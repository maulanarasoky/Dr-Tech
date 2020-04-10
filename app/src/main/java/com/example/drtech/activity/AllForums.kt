package com.example.drtech.activity

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drtech.R
import com.example.drtech.adapter.ForumsList
import com.example.drtech.interfaces.MyAsyncCallback
import com.example.drtech.model.Forum
import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_all_forums.*
import java.lang.ref.WeakReference

class AllForums : AppCompatActivity(), MyAsyncCallback {

    lateinit var database: DatabaseReference
    private lateinit var adapter: ForumsList

    var listForums: MutableList<Forum> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_forums)
        database = FirebaseDatabase.getInstance().reference

        HomeAsync(this).execute()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        forumRecyclerView.layoutManager = layoutManager
        forumRecyclerView.addItemDecoration(
            DividerItemDecoration(
                forumRecyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter = ForumsList(listForums)

        forumRecyclerView.adapter = adapter
    }

    private fun showForums() {
        try {
            database.child("Forums").addValueEventListener(object : ValueEventListener {
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
        listForums.clear()
        for (data in dataSnapshot.children) {
            val post = data.getValue(Forum::class.java)
            val x = Forum(
                dataSnapshot.child(post?.id.toString()).child("id").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("title").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("description").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("category").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("tags").value.toString(),
                dataSnapshot.child(post?.id.toString()).child("views").value.toString()
            )
            listForums.add(x)
        }
        adapter.notifyDataSetChanged()
    }

    inner class HomeAsync(listener: MyAsyncCallback) : AsyncTask<Void, Unit, Unit>() {

        private val myListener: WeakReference<MyAsyncCallback> = WeakReference(listener)

        override fun onPreExecute() {
            super.onPreExecute()
            val myListener = myListener.get()
            myListener?.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?) {
            showForums()
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
