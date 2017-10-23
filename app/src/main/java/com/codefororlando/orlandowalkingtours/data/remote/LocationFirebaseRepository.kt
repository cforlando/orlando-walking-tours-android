package com.codefororlando.orlandowalkingtours.data.remote

import com.codefororlando.orlandowalkingtours.data.entities.Coordinates
import com.codefororlando.orlandowalkingtours.data.entities.Location
import com.codefororlando.orlandowalkingtours.data.entities.RegistryDates
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter

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