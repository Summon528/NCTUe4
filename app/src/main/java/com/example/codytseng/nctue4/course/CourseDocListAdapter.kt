package com.example.codytseng.nctue4.course

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.R
import com.example.codytseng.nctue4.model.DocGroupItem
import kotlinx.android.synthetic.main.course_doc_group_list_card.view.*

class CourseDocListAdapter(private val DataSet: ArrayList<DocGroupItem>,
                           private val itemClickListener: (DocGroupItem) -> Unit) :
        RecyclerView.Adapter<CourseDocListAdapter.ViewHolder>() {

    class ViewHolder(private  val view: View,
                     private val itemClickListener: (DocGroupItem) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(doc: DocGroupItem) {
            view.doc_group_display_name.text = doc.displayName
            view.course_doc_group_list_item.setOnClickListener {
                itemClickListener(doc)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CourseDocListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_doc_group_list_card, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(DataSet[position])

    }

    override fun getItemCount() = DataSet.size
}