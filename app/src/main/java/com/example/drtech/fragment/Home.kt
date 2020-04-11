package com.example.drtech.fragment

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drtech.R
import com.example.drtech.activity.AllForums
import com.example.drtech.activity.Login
import com.example.drtech.adapter.ForumsList
import com.example.drtech.interfaces.MyAsyncCallback
import com.example.drtech.model.Forum
import com.example.drtech.model.Users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.startActivity
import java.lang.ref.WeakReference

/**
 * A simple [Fragment] subclass.
 */
class Home : Fragment(), MyAsyncCallback {

    val images = intArrayOf(R.drawable.sample1, R.drawable.sample2, R.drawable.sample3)

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    private lateinit var adapter: ForumsList

    var listForums: MutableList<Forum> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        HomeAsync(this).execute()

        initCarousel()

        allForums.setOnClickListener {
            startActivity<AllForums>()
        }

        allSpecialists.setOnClickListener {

        }

        search_bar.setOnClickListener {
            startActivity<Login>()
        }

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
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

    private fun initCarousel() {
        carouselView.setImageListener(imageListener)
        carouselView.pageCount = images.size
    }

    private val imageListener =
        ImageListener { position, imageView -> imageView.setImageResource(images[position]) }

    private fun showForums() {
        try {
            database.child("Forums").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    showData(p0)
                }

            })
            if (auth.currentUser != null) {
                database.child("Users").child(auth.currentUser?.uid.toString())
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

    private fun showData(dataSnapshot: DataSnapshot) {
        var count = 1
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
            count++
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
    }

    override fun onPostExecute() {
    }
}
