package com.team214.nctue4

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.team214.nctue4.utility.NewE3Connect
import com.team214.nctue4.utility.NewE3Interface

class NewE3Fragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getActivity()!!.setTitle(R.string.new_e3);

        return inflater.inflate(R.layout.fragment_blank, container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var newE3Service = NewE3Connect()
        newE3Service.getCookie { status, response ->
            when (status) {
                NewE3Interface.Status.SUCCESS -> {
//                    Toast.makeText(context, "get cookie", Toast.LENGTH_LONG).show()
                    getData(response!!)
                }
            }
        }

    }
    private fun getData(cookie: String){
        var newE3Service = NewE3Connect()
        newE3Service.getAnn(cookie!!) { status, response ->
            when (status) {
                NewE3Interface.Status.SUCCESS -> {
//                    Toast.makeText(context, "get ann", Toast.LENGTH_LONG).show()
                    Log.d("ya", "yaaaa")
                }
            }
        }
    }
}