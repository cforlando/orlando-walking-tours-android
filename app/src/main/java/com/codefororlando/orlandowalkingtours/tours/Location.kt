package com.codefororlando.orlandowalkingtours.tours

/**
 * Created by ryan on 7/31/17.
 */
data class Location(
        val name: String,
        val address: String,
        val description: String,
        val lat: Double,
        val lng: Double,
        val photos: List<String>,
        val importantDates: ImportantDates?
)