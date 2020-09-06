package com.atozkids.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.atozkids.R
import com.atozkids.data.DataManager
import com.atozkids.interfaces.OnQuizOptionClickListener
import com.atozkids.responsemodels.QuizResponseModel
import com.atozkids.ui.activities.QuizActivity
import com.atozkids.ui.adapters.QuizOptionsAdapter
import com.atozkids.utils.AudioUtils
import com.atozkids.utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_quiz_item.*

private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [QuizItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuizItemFragment : Fragment() {

    private lateinit var quizItem: QuizResponseModel.Datum

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            quizItem = it.getSerializable(ARG_PARAM1) as QuizResponseModel.Datum
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setQuizData()
    }

    private fun setQuizData() {
        txtQuestion.text = quizItem.questionText

        rcvOptions.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rcvOptions.layoutManager = linearLayoutManager

        rcvOptions.adapter =
            QuizOptionsAdapter(quizItem.options, object : OnQuizOptionClickListener {
                override fun onClick(view: View, option: QuizResponseModel.Datum.Option) {
                    if (option.isCorrect) {
                        if (DataManager.getSound()) {
                            AudioUtils.playResourceAudio(R.raw.success)
                        }
                        (activity as QuizActivity).setQuizCorrectCounter()
                    } else {
                        if (DataManager.getSound()) {
                            AudioUtils.playResourceAudio(R.raw.fail)
                        }
                        (activity as QuizActivity).setInCorrectCounter()

                    }
                }

            })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: QuizResponseModel.Datum) =
            QuizItemFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }
}