package com.envirocleanadmin.utils.custommarker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.envirocleanadmin.R
import com.envirocleanadmin.data.response.CommunityAreaListResponse
import com.envirocleanadmin.utils.AppConstants

class MarkerInfoWindowFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.custom_marker_info_window, container, false)
    }

    lateinit var markerEditAreaClickListener: MarkerEditAreaClickListener

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val result =
            arguments!!.getParcelable<CommunityAreaListResponse.Result>(AppConstants.COMMUNITY_DATA)!!
        (view.findViewById<View>(R.id.tvAreaName) as AppCompatTextView).text = result.areaName
        (view.findViewById<View>(R.id.tvAreaDistance) as AppCompatTextView).text =
            result.areaRange!!.toDouble().toInt().toString() + " meter away"

        val viewDirectionOnClickListener =
            View.OnClickListener { v: View? ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "https://www.google.com/maps/dir/?api=1&destination= ${result.areaLatitude!!.toDouble()}" +
                                ",${result.areaLongitude!!.toDouble()}"
                    )
                )
                context!!.startActivity(intent)
            }

        val editOnClickListener =
            View.OnClickListener { v: View? ->
                markerEditAreaClickListener.onMarkerEditAreaClick(result)
            }

        view.findViewById<View>(R.id.btnViewDirection)
            .setOnClickListener(viewDirectionOnClickListener)
        view.findViewById<View>(R.id.ivEditLocation)
            .setOnClickListener(editOnClickListener)

    }

    interface MarkerEditAreaClickListener {
        fun onMarkerEditAreaClick(result: CommunityAreaListResponse.Result)
    }
}