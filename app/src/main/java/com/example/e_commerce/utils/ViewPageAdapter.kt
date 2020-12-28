package com.example.e_commerce.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class ViewPageAdapter(
    fragmentManager: FragmentManager,
    private val fragmentList: MutableList<Fragment>
) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
        notifyDataSetChanged()
    }

}