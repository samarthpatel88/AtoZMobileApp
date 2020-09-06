package com.atozkids.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.atozkids.R
import com.atozkids.interfaces.OnQuizOptionClickListener
import com.atozkids.responsemodels.QuizResponseModel
import com.atozkids.utils.ImageUtils
import kotlinx.android.synthetic.main.row_item_quiz_option_list.view.*

class QuizOptionsAdapter(
    private val optionList: List<QuizResponseModel.Datum.Option>,
    private val clickListener: OnQuizOptionClickListener
) :
    RecyclerView.Adapter<QuizOptionsAdapter.MenuHolder>() {
    private var isFindCorrect: Boolean = false
    private var selected: Int = -1

    class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_quiz_option_list, parent, false)
        return MenuHolder(view)
    }

    override fun getItemCount(): Int {
        return optionList.size
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        val option = optionList[position]
        ImageUtils.showImageFromPath(option.itemImage, holder.itemView.imgOption)
        if (selected == option.itemId) {
            if (option.isCorrect) {
                holder.itemView.rlTrueAnswer.visibility = View.VISIBLE
            } else {
                holder.itemView.rlFalseAnswer.visibility = View.VISIBLE
            }
        }

        if (isFindCorrect && selected != option.itemId) {
            if (option.isCorrect) {
                holder.itemView.rlTrueAnswer.visibility = View.VISIBLE
                holder.itemView.startAnimation(
                    AnimationUtils.loadAnimation(
                        holder.itemView.context,
                        R.anim.shake
                    )
                )
            }
        }

        holder.itemView.setOnClickListener {
            if (selected == -1) {
                selected = optionList[position].itemId
                isFindCorrect = true
                notifyDataSetChanged()
                clickListener.onClick(holder.itemView, optionList[position])
            }
        }
    }
}
