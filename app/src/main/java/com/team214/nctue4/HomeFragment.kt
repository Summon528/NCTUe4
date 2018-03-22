package com.team214.nctue4


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentManager = activity!!.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putBoolean("home", true)
        val fragment1 = HomeAnnFragment()
        fragment1.arguments = bundle
        transaction.add(R.id.home_ann, fragment1)
        val fragment2 = DownloadFragment()
        fragment2.arguments = bundle
        transaction.add(R.id.home_download, fragment2)
        val fragment3 = StarredCoursesE3Fragment()
        fragment3.arguments = bundle
        transaction.add(R.id.home_starred, fragment3)
        transaction.commit()
        home_more_ann.setOnClickListener { (activity!! as MainActivity).switchFragment(R.id.nav_ann) }
        home_more_download.setOnClickListener { (activity!! as MainActivity).switchFragment(R.id.nav_download) }
        home_more_course.setOnClickListener { (activity!! as MainActivity).switchFragment(R.id.nav_starred_courses) }

//
    }

}// Required empty public constructor
