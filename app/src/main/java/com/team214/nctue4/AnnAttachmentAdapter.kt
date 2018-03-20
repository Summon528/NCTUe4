package com.team214.nctue4

import android.content.Context
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team214.nctue4.model.AttachItem
import com.team214.nctue4.utility.FileNameToColor
import com.team214.nctue4.utility.FileNameToIcon
import kotlinx.android.synthetic.main.item_announcement_attach.view.*

class AnnAttachmentAdapter(val context: Context,  val dataSet: ArrayList<AttachItem>,
                           private val itemClickListener: (AttachItem) -> Unit) :
        RecyclerView.Adapter<AnnAttachmentAdapter.ViewHolder>() {

   class ViewHolder(val context: Context, val view: View, private val itemClickListener: (AttachItem) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(attach: AttachItem) {
            view.announcement_attach_name.text = attach.name
            view.announcement_attach_img.setImageResource(FileNameToIcon().getId(attach.name))
            view.announcement_attach_img.setColorFilter(getColor(context, FileNameToColor().getId(attach.name)))
            view.announcement_attach_bar.setBackgroundColor(getColor(context, FileNameToColor().getId(attach.name)))
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
        return ViewHolder(context, view,itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size
}