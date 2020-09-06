package com.atozkids.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.atozkids.responsemodels.QuizResponseModel
import com.atozkids.ui.fragments.QuizItemFragment

class QuizFragmentsAdapter(
    fm: FragmentManager,
    private val quizList: List<QuizResponseModel.Datum>
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return QuizItemFragment.newInstance(quizList[position])
    }

    override fun getCount(): Int {
        return quizList.size
    }
}
