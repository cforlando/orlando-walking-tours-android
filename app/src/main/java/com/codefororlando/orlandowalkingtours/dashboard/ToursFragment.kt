package com.codefororlando.orlandowalkingtours.dashboard

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import com.codefororlando.orlandowalkingtours.R

/**
 * Created by ryan on 7/29/17.
 */
class ToursFragment : Fragment() {

    lateinit var tourGrid: GridView
    lateinit var emptyText: TextView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_tours, container, false)


        if (view != null){
            tourGrid = view.findViewById(R.id.tours_grid) as GridView
            emptyText = view.findViewById(R.id.tours_emptyGridText) as TextView
        }


        tourGrid.emptyView = emptyText

        return view
    }
}