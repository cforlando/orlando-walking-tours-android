package com.codefororlando.orlandowalkingtours.tours

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.graphics.BitmapCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.Base64
import android.util.JsonReader
import com.codefororlando.orlandowalkingtours.R
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.Reader

/**
 * Created by ryan on 8/1/17.
 */
class TourLocalRepository(val context: Context) : TourRepository {
// TODO: This is all hardcoded for the demo. Make this into something actually useful or remove it.

    val filename: String = "locations.json"
    val indexOfName = 12
    val indexOfAddress = 9
    val indexOfDescription = 14
    val indexOfLocationArray = 15

    //Within the array at index 15
    val indexOfLatitude = 1
    val indexOfLongitude = 2

    override fun getTours(): List<Tour> {

        val locations = getLocations()

        val fancyTour: Tour = Tour("Fancy Tour", listOf(locations[8], locations[43], locations[59], locations[54]))
        val bestPlacesTour: Tour = Tour("Best Locations in the World", listOf(locations[23], locations[25], locations[17]))
        val cityExpeditionTour: Tour = Tour("Orlando Expedition", listOf(locations[22], locations[44], locations[30], locations[7], locations[44], locations[10]))

        return listOf(fancyTour, bestPlacesTour, cityExpeditionTour)
    }

    fun getDataArray(): JSONArray {
        val input: InputStream? = context.assets.open(filename)
        val root: JSONObject = JSONObject(BufferedInputStream(input).bufferedReader().readText())

        return root.getJSONArray("data")
    }

    fun getLocations(): List<Location> {
        val data: JSONArray = getDataArray()

        val exampleLocations: MutableList<Location> = mutableListOf()

        for (i in 0..(data.length() - 1)) {
            val info: JSONArray = data[i] as JSONArray
            val locInfo: JSONArray = info[indexOfLocationArray] as JSONArray
            exampleLocations.add(Location(
                    info[indexOfName] as String,
                    info[indexOfAddress] as String,
                    info[indexOfDescription] as String,
                    (locInfo[indexOfLatitude] as String).toDouble(),
                    (locInfo[indexOfLongitude] as String).toDouble(),
                    emptyList(),
                    null))
        }

        return exampleLocations.toList()
    }

}