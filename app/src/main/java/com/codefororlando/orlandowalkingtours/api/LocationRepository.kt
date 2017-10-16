package com.codefororlando.orlandowalkingtours.api

import com.codefororlando.orlandowalkingtours.entities.Location
import io.reactivex.Flowable

/**
 * Created by ryan on 10/12/17.
 */
interface LocationRepository {
    fun getLocationsFrom(city: String): Flowable<Location>
}