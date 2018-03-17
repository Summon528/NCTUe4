package com.example.codytseng.nctue4

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.model.AttachItem
import kotlinx.android.synthetic.main.item_announcement_attach.view.*

class AnnAttachmentAdapter(private val dataSet: ArrayList<AttachItem>,
                           private val itemClickListener: (AttachItem) -> Unit) :
        RecyclerView.Adapter<AnnAttachmentAdapter.ViewHolder>() {

   class ViewHolder(val view: View, private val itemClickListener: (AttachItem) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(attach: AttachItem) {
            view.announcement_attach_name.text = attach.name
            view.announcement_attach_fileSize.text = attach.fileSize
            view.announcement_attach_button.setOnClickListener{
                itemClickListener(attach)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AnnAttachmentAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_announcement_attach, parent, false)
        return ViewHolder(view,itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size
}