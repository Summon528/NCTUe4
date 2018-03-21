package com.team214.nctue4


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team214.nctue4.utility.openFile
import kotlinx.android.synthetic.main.fragment_download.*
import kotlinx.android.synthetic.main.status_empty.*
import java.io.File
import kotlin.math.min

/**
 * A simple [Fragment] subclass.
 */
class DownloadFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val path = activity!!.getExternalFilesDir(null).toString()
        val dir = File(path + File.separator + "Download")
        if (dir.exists()) {
            val files: Array<File> = dir.listFiles()
            if(files.isEmpty()) empty_request.visibility = View.VISIBLE
            files.sortByDescending { it.lastModified() }
            download_recycler?.layoutManager = LinearLayoutManager(context)
            if (arguments?.getBoolean("home") != null)
                download_recycler?.isNestedScrollingEnabled = false
            download_recycler?.addItemDecoration(DividerItemDecoration(context,
                    LinearLayoutManager.VERTICAL))
            download_recycler?.adapter = DownloadAdapter(context!!,
                    if (arguments?.getBoolean("home") != null)
                        files.slice(0..minOf(4, files.size - 1))
                    else files.toList()) {
                openFile(it.name, it, context!!, activity!!)
            }
        }else{
            empty_request.visibility = View.VISIBLE
        }
    }

}// Required empty public constructor
