package com.atozkids.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.atozkids.ui.fragments.CategorySelectionFragment

class CategoryFragmentsAdapter(fm: FragmentManager, private val counts: Int,private val isFor:String) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return CategorySelectionFragment.newInstance(position,isFor)
    }

    override fun getCount(): Int {
        return counts
    }
}
