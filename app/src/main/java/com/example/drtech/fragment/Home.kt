package com.example.drtech.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.drtech.R
import com.example.drtech.activity.AllForums
import com.example.drtech.activity.AllSpecialists
import com.example.drtech.adapter.ForumsList
import com.example.drtech.adapter.SpecialistList
import com.example.drtech.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.startActivity


/**
 * A simple [Fragment] subclass.
 */
class Home : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    private lateinit var adapterForum: ForumsList
    private lateinit var adapterSpecialist: SpecialistList

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
            startActivity<AllSpecialists>()
        }

        val forumLayout = GridLayoutManager(context, 2)
        forumRecyclerView.layoutManager = forumLayout
        val specialistLayout = GridLayoutManager(context, 2)
        specialistRecyclerView.layoutManager = specialistLayout

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(HomeViewModel::class.java)
        mainViewModel.showForums(firstLetter, userName)
        mainViewModel.showSpecialist()
        mainViewModel.showTotal()
        showLoading(true)
        mainViewModel.getDataForums().observe(this, Observer { forumItems ->
            if (forumItems != null) {
                adapterForum = ForumsList(forumItems)
                forumRecyclerView.adapter = adapterForum
            }
        })
        mainViewModel.getDataSpecialists().observe(this, Observer { specialistItems ->
            if(specialistItems != null){
                adapterSpecialist = SpecialistList(specialistItems)
                specialistRecyclerView.adapter = adapterSpecialist
                showLoading(false)
                table.visibility = View.VISIBLE
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
