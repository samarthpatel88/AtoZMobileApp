package com.atozkids.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.atozkids.responsemodels.RhymesResponseModel
import com.atozkids.ui.fragments.RhymeFragment

class RhymesFragmentsAdapter(
    fm: FragmentManager,
    private val rhymesList: List<RhymesResponseModel.Datum>
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return RhymeFragment.newInstance(rhymesList[position])
    }

    override fun getCount(): Int {
        return rhymesList.size
    }
}
