package com.example.codytseng.nctue4

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.codytseng.nctue4.model.AnnItem
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var oldE3Service : OldE3Connect

    override fun onStop() {
        super.onStop()
        oldE3Service.cancelPendingRequests()
    }

    override fun onRefresh() {
        announcement_refreshLayout.isRefreshing = false
        ann_login_recycler_view.adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.home_fragment, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oldE3Service = (activity as MainActivity).oldE3Service
        announcement_refreshLayout.setOnRefreshListener(this)
        getData()
    }

    private fun getData() {
        oldE3Service.getAnnouncementListLogin { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    updateList(response!!)
                }
            }
        }
    }

    private fun updateList(annItems: ArrayList<AnnItem>) {
        ann_login_recycler_view?.layoutManager = LinearLayoutManager(context)
        ann_login_recycler_view?.addItemDecoration(DividerItemDecoration(context,
                LinearLayoutManager.VERTICAL))
        ann_login_recycler_view?.adapter = HomeAnnAdapter(annItems) {
            val intent = Intent()
            intent.setClass(activity, AnnActivity::class.java)
            intent.putExtra("annId", it.bulletinId)
            intent.putExtra("courseName", it.courseName)
            startActivity(intent)
        }
    }
}

