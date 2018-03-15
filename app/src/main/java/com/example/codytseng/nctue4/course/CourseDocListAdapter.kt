package com.example.codytseng.nctue4.course

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.R
import com.example.codytseng.nctue4.model.DocGroupItem
import kotlinx.android.synthetic.main.course_doc_group_list_card.view.*

class CourseDocListAdapter(val myDataset: ArrayList<DocGroupItem>, val itemClickListener: (DocGroupItem)->Unit) :
        RecyclerView.Adapter<CourseDocListAdapter.ViewHolder>() {

    class ViewHolder(val view: View, val itemClickListener: (DocGroupItem) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(doc: DocGroupItem) {
            view.doc_group_display_name.text = doc.displayName
            view.course_doc_group_list_item.setOnClickListener{
                itemClickListener(doc)
            }
        }
    }



    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CourseDocListAdapter.ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_doc_group_list_card, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(view, itemClickListener)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(myDataset[position])

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}