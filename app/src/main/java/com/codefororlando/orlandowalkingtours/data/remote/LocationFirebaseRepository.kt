package com.codefororlando.orlandowalkingtours.data.remote

import com.codefororlando.orlandowalkingtours.data.entities.Coordinates
import com.codefororlando.orlandowalkingtours.data.entities.Location
import com.codefororlando.orlandowalkingtours.data.entities.RegistryDates
import com.google.firebase.database.*
import io.reactivex.*

/**
 * Created by ryan on 10/12/17.
 */
class LocationFirebaseRepository() : LocationRepository {

    private val database = FirebaseDatabase.getInstance()

    override fun getLocationsFrom(city: String): Flowable<Location> {
        val cityRef = database.getReference("historic-locations/" + city)

        return Flowable.create({ emitter: FlowableEmitter<Location> ->
            cityRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        emitter.onNext(it.extractLocation())
                    }
                    emitter.onComplete()
                }
            })
        }, BackpressureStrategy.BUFFER)
    }

    override fun getLocationById(id: String): Single<Location> {
        val locationRef = database.getReference("historic-locations/orlando")

        return Single.create { emitter: SingleEmitter<Location> ->
            locationRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) = emitter.onError(Throwable("Database cancelled"))

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChild(id))
                        emitter.onSuccess(p0.child(id).extractLocation())
                    else
                        emitter.onError(Throwable("Location $id not found"))
                }

            })
        }
    }

    private fun DataSnapshot.extractLocation(): Location {
        val id = this.key
        val name = child("name").getValue(String::class.java)
        val address = child("address").getValue(String::class.java)
        val description = child("description").getValue(String::class.java)
        val coordinates = child("location").extractCoordinates()
        val pictures = listOf<String>() // TODO: Support pictures
        val registryDates = extractRegistryDates()

        return Location(id, name, address, description, coordinates, pictures, registryDates)
    }

    private fun DataSnapshot.extractCoordinates(): Coordinates {
        val latitude = child("latitude").getValue(Double::class.java)
        val longitude = child("longitude").getValue(Double::class.java)

        return Coordinates(latitude, longitude)
    }

    private fun DataSnapshot.extractRegistryDates(): RegistryDates {
        val local = if (hasChild("localRegistryDate")) child("localRegistryDate").getValue(String::class.java) else ""
        val national = if (hasChild("nationalRegistryDate")) child("nationalRegistryDate").getValue(String::class.java) else ""

        return RegistryDates(local, national)
    }
}