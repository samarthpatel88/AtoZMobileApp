package com.atozkids.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atozkids.R
import com.atozkids.responsemodels.CategoryItemsResponseModel
import com.atozkids.ui.activities.LearningActivity
import com.atozkids.utils.FileUtils
import com.atozkids.utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_learning_item.*

private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [LearningItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LearningItemFragment : Fragment() {

    private lateinit var mItem: CategoryItemsResponseModel.Datum

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mItem = it.getSerializable(ARG_PARAM1) as CategoryItemsResponseModel.Datum
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_learning_item, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: CategoryItemsResponseModel.Datum) =
            LearningItemFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FileUtils.getRealPhotoFile(mItem.itemname + LearningActivity.FileConcat.ITEM_IMAGE).let {
            if (it.exists()) {
                ImageUtils.showImageFromFile(it, imgItem)
            } else {
                ImageUtils.showImageFromPath(mItem.itemimage, imgItem)
            }
        }

        FileUtils.getRealPhotoFile(mItem.itemname + LearningActivity.FileConcat.CAPTION_IMAGE).let {
            if (it.exists()) {
                ImageUtils.showImageFromFile(it, imgItemCaption)
            } else {
                ImageUtils.showImageFromPath(mItem.captionimage, imgItemCaption)
            }
        }

    }
}
