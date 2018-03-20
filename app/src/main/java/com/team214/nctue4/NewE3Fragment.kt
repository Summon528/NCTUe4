package com.team214.nctue4

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class NewE3Fragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getActivity()!!.setTitle(R.string.new_e3);
        return inflater.inflate(R.layout.fragment_blank, container,false)
    }

}