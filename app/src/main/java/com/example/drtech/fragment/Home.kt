package com.example.drtech.fragment

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drtech.R
import com.example.drtech.activity.AllForums
import com.example.drtech.activity.Search
import com.example.drtech.adapter.ForumsList
import com.example.drtech.interfaces.MyAsyncCallback
import com.example.drtech.model.Forum
import com.example.drtech.model.Users
import com.example.drtech.viewmodel.HomeViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.startActivity
import java.lang.ref.WeakReference


/**
 * A simple [Fragment] subclass.
 */
class Home : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    private lateinit var adapter: ForumsList

    var listForums: MutableList<Forum> = mutableListOf()

    private lateinit var mainViewModel: HomeViewModel

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

        forum.setOnClickListener {
            startActivity<AllForums>()
        }

        specialist.setOnClickListener {

        }

        val layoutManager = GridLayoutManager(context, 2)
        forumRecyclerView.layoutManager = layoutManager

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(HomeViewModel::class.java)
        mainViewModel.showForums(firstLetter, userName)
        mainViewModel.showTotal()
        showLoading(true)
        mainViewModel.getForums().observe(this, Observer { forumItems ->
            if (forumItems != null) {
                adapter = ForumsList(forumItems)
                forumRecyclerView.adapter = adapter
                showLoading(false)
                forum.visibility = View.VISIBLE
                specialist.visibility = View.VISIBLE
                titleForum.visibility = View.VISIBLE
                forumRecyclerView.visibility = View.VISIBLE
                titleSpecialist.visibility = View.VISIBLE
                specialistRecyclerView.visibility = View.VISIBLE
            }
        })
        mainViewModel.getTotalForums().observe(this, Observer { totalForum ->
            totalForums.text =  "$totalForum Forum"
        })

        mainViewModel.getTotalSpecialist().observe(this, Observer { totalSpecialist ->
            totalSpecialists.text =  "$totalSpecialist Specialist"
        })

        mainViewModel.getTotalTags().observe(this, Observer { totalTag ->
            totalTags.text =  "$totalTag Tag"
        })
    }

    private fun showLoading(state: Boolean){
        if (state){
            progressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.GONE
        }
    }
}
