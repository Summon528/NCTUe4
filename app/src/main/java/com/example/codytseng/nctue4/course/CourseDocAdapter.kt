package com.example.codytseng.nctue4.course

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.R
import com.example.codytseng.nctue4.model.DocItem
import kotlinx.android.synthetic.main.course_doc_card.view.*

class CourseDocAdapter(private val dataSet: ArrayList<DocItem>,
                       private val itemClickListener: (DocItem) -> Unit) :
        RecyclerView.Adapter<CourseDocAdapter.ViewHolder>() {
    class ViewHolder(val view: View, private val itemClickListener: (DocItem) -> Unit) :
            RecyclerView.ViewHolder(view) {
        fun bind(doc: DocItem) {
            view.doc_display_name.text = doc.displayName
            view.course_doc_card_layout.setOnClickListener {
                itemClickListener(doc)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CourseDocAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_doc_card, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

    }

    override fun getItemCount() = dataSet.size
}