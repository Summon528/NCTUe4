package com.example.codytseng.nctue4.course

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.R
import com.example.codytseng.nctue4.model.DocItem
import kotlinx.android.synthetic.main.single_doc_card.view.*

class CourseDocAdapter(val myDataset: ArrayList<DocItem>, val itemClickListener: (DocItem) -> Unit) :
        RecyclerView.Adapter<CourseDocAdapter.ViewHolder>() {

    class ViewHolder(val view: View, val itemClickListener: (DocItem) -> Unit) :
            RecyclerView.ViewHolder(view) {
        fun bind(doc: DocItem) {
            view.doc_display_name.text = doc.displayName
            view.single_doc_card_layout.setOnClickListener {
                itemClickListener(doc)
            }
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CourseDocAdapter.ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_doc_card, parent, false)
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