package com.atozkids.ui.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.atozkids.R
import com.atozkids.app.App
import com.atozkids.data.DataManager
import com.atozkids.interfaces.OnAnimatedClick
import com.atozkids.interfaces.OnCategoryClickListener
import com.atozkids.responsemodels.CategoryListResponseModel
import com.atozkids.ui.activities.CategorySelectionActivity
import com.atozkids.ui.activities.LearningActivity
import com.atozkids.ui.activities.QuizActivity
import com.atozkids.ui.adapters.CategoryListAdapter
import com.atozkids.utils.Utility
import com.atozkids.utils.animatedClick
import com.atozkids.utils.isNetConnected
import com.atozkids.utils.openActivity
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.fragment_category_selection.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategorySelectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategorySelectionFragment : Fragment() {
    private var mPosition: Int = 0
    private var mIsFor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPosition = it.getInt(ARG_PARAM1)
            mIsFor = it.getString(ARG_PARAM2, CategorySelectionActivity.CategoryClass.LEARNING)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_selection, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int, isFor: String) =
            CategorySelectionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, position)
                    putString(ARG_PARAM2, isFor)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayData()
    }

    private fun displayData() {
        rcvCategory.isNestedScrollingEnabled = false
        rcvCategory.layoutManager = GridLayoutManager(activity, 4)

        val allList = DataManager.getCategoryListData()!!.data
        val data: ArrayList<CategoryListResponseModel.Datum> = ArrayList()

        var bunch = (mPosition * 8) + 8

        if (bunch > allList.size) {
            bunch = allList.size
        }

        for (i in (mPosition * 8) until bunch) {
            data.add(allList[i])

            if (i == (bunch - 1)) {
                rcvCategory.adapter = CategoryListAdapter(data,
                    object : OnCategoryClickListener {
                        override fun onClick(
                            view: View,
                            category: CategoryListResponseModel.Datum
                        ) {
                            view.animatedClick(object : OnAnimatedClick {
                                override fun onClick() {
                                    Permissions.check(
                                        activity /*context*/,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        null,
                                        object : PermissionHandler() {
                                            override fun onGranted() {
                                                if (mIsFor == CategorySelectionActivity.CategoryClass.LEARNING) {
                                                    openActivity(LearningActivity::class.java) {
                                                        putSerializable(
                                                            LearningActivity.ParcelData.CATEGORY,
                                                            category
                                                        )
                                                    }
                                                } else {
                                                    if (isNetConnected()){
                                                        openActivity(QuizActivity::class.java) {
                                                            putSerializable(
                                                                QuizActivity.ParcelData.CATEGORY,
                                                                category
                                                            )
                                                        }
                                                    }

                                                }
                                            }
                                        })
                                }
                            })
                        }

                    })
            }
        }

    }
}
