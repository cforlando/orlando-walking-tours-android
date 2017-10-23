package com.codefororlando.orlandowalkingtours.data.remote

import com.codefororlando.orlandowalkingtours.data.entities.Location
import io.reactivex.Flowable

/**
 * Created by ryan on 10/12/17.
 */
interface LocationRepository {
    fun getLocationsFrom(city: String): Flowable<Location>
}