package com.atozkids.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.atozkids.responsemodels.CategoryItemsResponseModel
import com.atozkids.ui.fragments.LearningItemFragment

class LearningItemsAdapter(fm: FragmentManager, private val items:List<CategoryItemsResponseModel.Datum>) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return LearningItemFragment.newInstance(items[position])
    }

    override fun getCount(): Int {
        return items.size
    }
}