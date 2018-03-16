package com.example.codytseng.nctue4.course

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.R
import com.example.codytseng.nctue4.model.AnnItem
import com.example.codytseng.nctue4.utility.htmlCleaner
import kotlinx.android.synthetic.main.course_announcement_card.view.*

class CourseAnnAdapter(private val dataSet: ArrayList<AnnItem>,
                       private val itemClickListener: (AnnItem) -> Unit) :
        RecyclerView.Adapter<CourseAnnAdapter.ViewHolder>() {

    class ViewHolder(val view: View,
                     private val itemClickListener: (AnnItem) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(ann: AnnItem) {
            view.announcement_caption.text = ann.caption
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                view.announcement_content.text = Html.fromHtml(htmlCleaner(ann.content))
                        .replace("\\s+".toRegex(), " ")
            } else {
                view.announcement_content.text = Html.fromHtml(htmlCleaner(ann.content),
                        Html.FROM_HTML_MODE_COMPACT).replace("\\s+".toRegex(), " ")
            }
            view.announcement_beginDate.text = ann.beginDate
            view.setOnClickListener{
                itemClickListener(ann)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_announcement_card, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size
}