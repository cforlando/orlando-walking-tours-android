package com.codefororlando.orlandowalkingtours.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.codefororlando.orlandowalkingtours.R
import com.codefororlando.orlandowalkingtours.tours.Tour

/**
 * Created by ryan on 7/31/17.
 */
class TourAdapter(val context: Context, val tourList: List<Tour>) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)


    override fun getItem(position: Int): Any = tourList[position]

    override fun getItemId(position: Int): Long = tourList[position].hashCode().toLong()

    override fun getCount(): Int = tourList.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        var holder: Holder

        if(view == null){
            view = layoutInflater.inflate(R.layout.grid_tour, null)

            holder = Holder()

            holder.textView = view.findViewById(R.id.grid_tourText) as TextView
            holder.imageView = view.findViewById(R.id.grid_tourImage) as ImageView

            view.tag = holder
        }
        else {
            holder = view.tag as Holder
        }

        holder.textView.text = tourList[position].name
        // TODO: Get image somehow

        return view!!
    }
}

class Holder {
    lateinit var textView: TextView
    lateinit var imageView: ImageView
}
