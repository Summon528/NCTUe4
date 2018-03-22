package com.team214.nctue4

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team214.nctue4.model.CourseItem
import kotlinx.android.synthetic.main.item_course.view.*

class CourseAdapter(private val dataSet: ArrayList<CourseItem>,
                    private val oldE3Starred: HashSet<String>,
                    private val context: Context?,
                    private val starClickListener: ((view: View, courseId: String) -> Unit),
                    private val itemClickListener: (CourseItem) -> Unit) :
        RecyclerView.Adapter<CourseAdapter.ViewHolder>() {
    class ViewHolder(val view: View,
                     private val oldE3Starred: HashSet<String>,
                     private val context: Context?,
                     private val starClickListener: ((view: View, courseId: String) -> Unit),
                     private val itemClickListener: (CourseItem) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(course: CourseItem) {
            view.course_name.text = course.courseName
            view.course_teacher.text = course.teacherName
            view.course_item.setOnClickListener {
                itemClickListener(course)
            }

            if (oldE3Starred.contains(course.courseId)) {
                view.course_star.setColorFilter(ContextCompat.getColor(context!!, R.color.md_orange_500))
            }
            view.course_star.setOnClickListener {
                starClickListener(view, course.courseId)
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CourseAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course, parent, false)
        return ViewHolder(view, oldE3Starred, context, starClickListener, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

    }

    override fun getItemCount() = dataSet.size
}