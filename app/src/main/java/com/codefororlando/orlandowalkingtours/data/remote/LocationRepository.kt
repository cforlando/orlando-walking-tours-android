package com.codefororlando.orlandowalkingtours.data.remote

import com.codefororlando.orlandowalkingtours.data.entities.Location
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by ryan on 10/12/17.
 */
interface LocationRepository {
    fun getLocations(): Flowable<Location>
    fun getLocationById(id: String): Single<Location>
}