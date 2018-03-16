package com.example.codytseng.nctue4

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.model.AttachItem
import kotlinx.android.synthetic.main.announcement_attach_card.view.*

class AnnAttachmentAdapter(val myDataset: ArrayList<AttachItem>, val itemClickListener: (AttachItem)->Unit) :
        RecyclerView.Adapter<AnnAttachmentAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val view: View, val itemClickListener: (AttachItem) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(attach: AttachItem) {
            view.announcement_attach_name.text = attach.mName
//            view.announcement_attach_url.text = attach.mUrl
            view.announcement_attach_fileSize.text = attach.mFileSize

        }
    }



    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AnnAttachmentAdapter.ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.announcement_attach_card, parent, false)
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