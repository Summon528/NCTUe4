package com.team214.nctue4.course

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team214.nctue4.R
import com.team214.nctue4.model.AttachItem
import com.team214.nctue4.utility.FileNameToIcon
import kotlinx.android.synthetic.main.item_course_doc.view.*

class CourseDocDialogAdapter(private val dataSet: ArrayList<AttachItem>,
                             private val itemClickListener: (AttachItem) -> Unit) :
        RecyclerView.Adapter<CourseDocDialogAdapter.ViewHolder>() {
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
                                    viewType: Int): CourseDocDialogAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_doc, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

    }

    override fun getItemCount() = dataSet.size
}