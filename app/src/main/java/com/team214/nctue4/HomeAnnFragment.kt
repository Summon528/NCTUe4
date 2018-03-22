package com.team214.nctue4

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team214.nctue4.model.AnnItem
import com.team214.nctue4.utility.*
import kotlinx.android.synthetic.main.fragment_ann.*
import kotlinx.android.synthetic.main.status_empty.*
import kotlinx.android.synthetic.main.status_error.*


class HomeAnnFragment : Fragment()/*, SwipeRefreshLayout.OnRefreshListener*/ {
    private lateinit var oldE3Service: OldE3Connect
    private lateinit var newE3Service: NewE3Connect
    private var dataStatus = DataStatus.INIT
    private var oldE3get = false
    private var newE3get = false
    private var oldE3AnnItems = ArrayList<AnnItem>()
    private var newE3AnnItems = ArrayList<AnnItem>()

    override fun onStop() {
        super.onStop()
        oldE3Service.cancelPendingRequests()
        if (dataStatus == DataStatus.INIT) dataStatus = DataStatus.STOPPED
    }

    override fun onStart() {
        super.onStart()
        if (dataStatus == DataStatus.STOPPED) getData()
    }

//    override fun onRefresh() {
//        announcement_refreshLayout.isRefreshing = false
//        ann_login_recycler_view.adapter.notifyDataSetChanged()
//    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getActivity()!!.setTitle(R.string.app_name);
        return inflater.inflate(R.layout.fragment_ann, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }
    private fun race() {
        if (oldE3get && newE3get){
            val annItems = ArrayList<AnnItem>()
            annItems.addAll(newE3AnnItems)
            annItems.addAll(oldE3AnnItems)
            (activity as MainActivity).runOnUiThread{
                Runnable {
                    annItems.sortByDescending { it.beginDate }
                    updateList(annItems)
                    dataStatus = DataStatus.FINISHED
                    progress_bar.visibility = View.GONE
                }.run()
            }
        }
    }

    private fun getData() {
        error_request?.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE

        oldE3Service = (activity as MainActivity).oldE3Service
//        announcement_refreshLayout.setOnRefreshListener(this)
        oldE3Service.getAnnouncementListLogin { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    oldE3AnnItems = response!!
                    oldE3get = true
                    race()
                }
                else -> {
                    (activity as MainActivity).runOnUiThread{
                        Runnable {
                            error_request.visibility = View.VISIBLE
                            dataStatus = DataStatus.INIT
                            error_request_retry.setOnClickListener { getData() }
                        }.run()
                    }
                }
            }
        }

        newE3Service = (activity as MainActivity).newE3Service
        newE3Service.getAnn { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    newE3AnnItems = response!!
                    newE3get = true
                    race()
                }
                else -> {
                    (activity as MainActivity).runOnUiThread{
                        Runnable {
                            error_request.visibility = View.VISIBLE
                            dataStatus = DataStatus.INIT
                            error_request_retry.setOnClickListener { getData() }
                        }.run()
                    }
                }
            }
        }
    }

    private fun updateList(annItems: ArrayList<AnnItem>) {
        if (annItems.size == 0) {
            empty_request.visibility = View.VISIBLE
        } else {
            ann_login_recycler_view?.layoutManager = LinearLayoutManager(context)
            ann_login_recycler_view?.addItemDecoration(DividerItemDecoration(context,
                    LinearLayoutManager.VERTICAL))
            val fromHome = arguments?.getBoolean("home") != null
            if (fromHome) ann_login_recycler_view?.isNestedScrollingEnabled = false
            ann_login_recycler_view?.adapter = HomeAnnAdapter(
                    if (fromHome) annItems.slice(0..minOf(4, annItems.size - 1))
                    else annItems.toList(), fromHome) {

                Log.d("dd", "start ann acti")
                val intent = Intent()
                intent.setClass(activity, AnnActivity::class.java)
                intent.putExtra("annId", it.bulletinId)
                intent.putExtra("courseName", it.courseName)
                intent.putExtra("loginTicket", oldE3Service.getCredential().first)
                intent.putExtra("accountId", oldE3Service.getCredential().second)
                intent.putExtra("courseId", it.courseId)
                intent.putExtra("from", OldE3AnnFrom.HOME)
                intent.putExtra("newE3Cookie", newE3Service.getCredential())
                startActivity(intent)
            }
            ann_login_recycler_view.visibility = View.VISIBLE
//            announcement_refreshLayout.visibility = View.VISIBLE
        }
        oldE3get = false
        newE3get = false
    }
}

