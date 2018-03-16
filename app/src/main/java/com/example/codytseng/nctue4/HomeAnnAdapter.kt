package com.example.codytseng.nctue4

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.model.AnnItem
import kotlinx.android.synthetic.main.home_announcement_card.view.*

class HomeAnnAdapter(private val dataSet: ArrayList<AnnItem>,
                     private val itemClickListener: (AnnItem) -> Unit) :
        RecyclerView.Adapter<HomeAnnAdapter.ViewHolder>() {

    class ViewHolder(val view: View,
                     private val itemClickListener: (AnnItem) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(ann: AnnItem) {
            view.announcement_name_in_image.text = ann.courseName.first().toString()
            view.announcement_name.text = ann.courseName
            view.announcement_caption.text = ann.caption
            view.announcement_beginDate.text = ann.beginDate
            view.setOnClickListener() {
                itemClickListener(ann)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): HomeAnnAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.home_announcement_card, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size
}