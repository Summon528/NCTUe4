package com.example.codytseng.nctue4

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.model.AnnouncementItem
import kotlinx.android.synthetic.main.home_announcement_card.view.*

class HomeAnnAdapter(val myDataset: ArrayList<AnnouncementItem>, val itemClickListener: (AnnouncementItem)->Unit) :
        RecyclerView.Adapter<HomeAnnAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val view: View, val itemClickListener: (AnnouncementItem) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(announcement: AnnouncementItem) {
            view.announcement_name_in_image.text = announcement.courseName.first().toString()
            view.announcement_name.text = announcement.courseName
            view.announcement_caption.text = announcement.caption
//            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
//                view.announcement_content.text = Html.fromHtml(announcement.content).take(50)
//            } else {
//                view.announcement_content.text = Html.fromHtml(announcement.content, Html.FROM_HTML_MODE_COMPACT).take(50)
//            }
            view.announcement_beginDate.text = announcement.beginDate
            view.setOnClickListener(){
                itemClickListener(announcement)
            }        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): HomeAnnAdapter.ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.home_announcement_card, parent, false)
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