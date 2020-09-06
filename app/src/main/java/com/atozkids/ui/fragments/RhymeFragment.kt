package com.atozkids.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atozkids.R
import com.atozkids.responsemodels.RhymesResponseModel
import com.atozkids.utils.FileUtils
import com.atozkids.utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_rhyme.*

private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [RhymeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RhymeFragment : Fragment() {
    private lateinit var rhymeData: RhymesResponseModel.Datum

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            rhymeData = it.getSerializable(ARG_PARAM1) as RhymesResponseModel.Datum
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rhyme, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayRhyme()
    }

    private fun displayRhyme() {
        if (FileUtils.getRealPhotoFile(rhymeData.title).exists()) {
            ImageUtils.showImageFromFile(FileUtils.getRealPhotoFile(rhymeData.title), imgRhyme)
        } else {
            ImageUtils.showImageFromPath(rhymeData.image, imgRhyme)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: RhymesResponseModel.Datum) =
            RhymeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }
}