package com.example.codytseng.nctue4.course

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.R
import com.example.codytseng.nctue4.model.AttachItem
import com.example.codytseng.nctue4.utility.FileNameToIcon
import kotlinx.android.synthetic.main.item_course_doc.view.*

class CourseDocAdapter(private val dataSet: ArrayList<AttachItem>,
                       private val itemClickListener: (AttachItem) -> Unit) :
        RecyclerView.Adapter<CourseDocAdapter.ViewHolder>() {
    class ViewHolder(val view: View, private val itemClickListener: (AttachItem) -> Unit) :
            RecyclerView.ViewHolder(view) {
        fun bind(attach: AttachItem) {
            view.doc_display_name.text = attach.name
            view.doc_file_icon.setImageResource(FileNameToIcon().getId(attach.name))
            view.course_doc_card_layout.setOnClickListener {
                itemClickListener(attach)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CourseDocAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_doc, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

    }

    override fun getItemCount() = dataSet.size
}