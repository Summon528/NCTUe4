package com.team214.nctue4

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.team214.nctue4.utility.NewE3Connect
import com.team214.nctue4.utility.NewE3Interface

class NewE3Fragment : Fragment() {

    private lateinit var newE3Service: NewE3Connect
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getActivity()!!.setTitle(R.string.new_e3);

        return inflater.inflate(R.layout.fragment_blank, container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()

    }
    private fun getData(){
        val newE3Service = (activity as MainActivity).newE3Service
        newE3Service.getAnn { status, response ->
            when (status) {
                NewE3Interface.Status.SUCCESS -> {
                    Log.d("ya", "yaaaa")
                }
            }
        }
    }
}