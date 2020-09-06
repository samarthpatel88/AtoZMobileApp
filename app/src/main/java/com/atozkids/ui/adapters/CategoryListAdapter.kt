package com.atozkids.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atozkids.R
import com.atozkids.interfaces.OnCategoryClickListener
import com.atozkids.responsemodels.CategoryListResponseModel
import com.atozkids.utils.FileUtils
import com.atozkids.utils.ImageUtils
import kotlinx.android.synthetic.main.row_item_category_list.view.*

class CategoryListAdapter(
    private val categoryList: List<CategoryListResponseModel.Datum>,
    private val clickListener: OnCategoryClickListener
) :
    RecyclerView.Adapter<CategoryListAdapter.MenuHolder>() {
    class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(category: CategoryListResponseModel.Datum) {
            FileUtils.getRealPhotoFile(category.categoryname).let {
                if (it.exists()) {
                    ImageUtils.showImageFromFile(it, itemView.imgCategory)
                } else {
                    ImageUtils.showImageFromPath(category.categoryicon, itemView.imgCategory)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_category_list, parent, false)
        return MenuHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        holder.bindData(categoryList[position])
        holder.itemView.setOnClickListener {
            clickListener.onClick(holder.itemView, categoryList[position])
        }
    }
}