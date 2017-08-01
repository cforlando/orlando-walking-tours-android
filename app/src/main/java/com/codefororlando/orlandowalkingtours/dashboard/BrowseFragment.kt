package com.codefororlando.orlandowalkingtours.dashboard

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.codefororlando.orlandowalkingtours.R
import com.codefororlando.orlandowalkingtours.tours.TourLocalRepository

/**
 * Created by ryan on 7/29/17.
 */
class BrowseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater?.inflate(R.layout.fragment_browse, container, false)!!

        val tourGrid: GridView = view.findViewById(R.id.browse_grid) as GridView

        tourGrid.adapter = TourAdapter(context, TourLocalRepository(context).getTours())

        return view
    }
}