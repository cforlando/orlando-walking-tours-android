package com.codefororlando.orlandowalkingtours.tours

/**
 * Created by ryan on 8/1/17.
 */
interface TourRepository {
    fun getTours(): List<Tour>
}