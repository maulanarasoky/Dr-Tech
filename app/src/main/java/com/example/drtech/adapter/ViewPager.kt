package com.example.drtech.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class ViewPager(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val fragmentList: MutableList<Fragment> = mutableListOf()
    private val fragmentListTitle: MutableList<String> = mutableListOf()

    override fun getItem(position: Int) = fragmentList[position]

    override fun getCount() = fragmentListTitle.size

    override fun getPageTitle(position: Int) = fragmentListTitle[position]

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentListTitle.add(title)
    }
}