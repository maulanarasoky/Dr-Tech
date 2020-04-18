package com.example.drtech.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drtech.R
import com.example.drtech.adapter.ForumsList
import com.example.drtech.model.Forum
import com.example.drtech.viewmodel.SearchViewModel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_search.*

class Search : AppCompatActivity() {

    companion object{
        const val TAGS = "TAGS"
    }

    lateinit var database: DatabaseReference

    lateinit var mainViewModel: SearchViewModel

    lateinit var adapter: ForumsList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        database = FirebaseDatabase.getInstance().reference

        val linearLayout = GridLayoutManager(this, 2)
        commentsRecyclerView.layoutManager = linearLayout

        searchWhat.text = "Menampilkan \'${intent.getStringExtra(TAGS)}\'..."

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SearchViewModel::class.java)
        intent?.getStringExtra(TAGS)?.let { mainViewModel.searchForum(it) }
        showLoading(true)
        mainViewModel.getForums().observe(this, Observer { forumItems ->
            if (forumItems != null) {
                adapter = ForumsList(forumItems)
                commentsRecyclerView.adapter = adapter
                showLoading(false)
                commentsRecyclerView.visibility = View.VISIBLE
                Log.d("DATA", forumItems.toString())
            }
        })

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun showLoading(state: Boolean){
        if (state){
            progressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.GONE
        }
    }

}
