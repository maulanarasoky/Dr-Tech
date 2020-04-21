package com.example.drtech.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.drtech.R
import com.example.drtech.activity.AllForums
import com.example.drtech.activity.AllSpecialists
import com.example.drtech.adapter.ForumsList
import com.example.drtech.adapter.SpecialistList
import com.example.drtech.model.Users
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

    var status = ""

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

        showRegularName()

        forum.setOnClickListener {
            startActivity<AllForums>()
        }

        specialist.setOnClickListener {
            if (status == "Specialist") {
                val dialog = SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                dialog.setCustomImage(resources.getDrawable(R.drawable.ic_dr_tech))
                dialog.titleText = "Fitur ini tidak tersedia bagi Specialist"
                dialog.setCancelable(false)
                dialog.show()
                return@setOnClickListener
            }
            startActivity<AllSpecialists>()
        }

        val forumLayout = GridLayoutManager(context, 2)
        forumRecyclerView.layoutManager = forumLayout
        val specialistLayout = GridLayoutManager(context, 2)
        specialistRecyclerView.layoutManager = specialistLayout

        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(HomeViewModel::class.java)
        mainViewModel.showForums(firstLetter, userName)
        mainViewModel.showSpecialist()
        mainViewModel.showTotal()
        showLoading(true)
        mainViewModel.getDataForums().observe(this, Observer { forumItems ->
            if (forumItems.size > 0) {
                adapterForum = ForumsList(forumItems)
                forumRecyclerView.adapter = adapterForum
            }
        })
        mainViewModel.getDataSpecialists().observe(this, Observer { specialistItems ->
            if (specialistItems.size > 0) {
                showLoading(false)
                adapterSpecialist = SpecialistList(specialistItems)
                specialistRecyclerView.adapter = adapterSpecialist
                table.visibility = View.VISIBLE
                titleForum.visibility = View.VISIBLE
                forumRecyclerView.visibility = View.VISIBLE
                titleSpecialist.visibility = View.VISIBLE
                specialistRecyclerView.visibility = View.VISIBLE
            }
        })
        mainViewModel.getTotalForums().observe(this, Observer { totalForum ->
            totalForums.text = "$totalForum Forum"
        })

        mainViewModel.getTotalSpecialist().observe(this, Observer { totalSpecialist ->
            totalSpecialists.text = "$totalSpecialist Specialist"
        })

        mainViewModel.getTotalTags().observe(this, Observer { totalTag ->
            totalTags.text = "$totalTag Tag"
        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    fun showRegularName() {
        database.child("Users").child("Regular").child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val data = p0.getValue(Users::class.java)
                    if (data == null || data.toString() == "null") {
                        showSpecialistName()
                    } else {
                        status = "Regular"
                    }
                }
            })

    }

    fun showSpecialistName() {
        database.child("Users").child("Specialist").child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val data = p0.getValue(Users::class.java)
                    if (data != null || data.toString() != "null") {
                        status = "Specialist"
                    }
                }
            })
    }
}
