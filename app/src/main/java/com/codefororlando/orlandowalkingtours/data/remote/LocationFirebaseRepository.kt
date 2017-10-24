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
    private val orlandoRef = database.getReference("historic-locations/orlando")

    override fun getLocations(): Flowable<Location> = Flowable.create({ emitter: FlowableEmitter<Location> ->
        orlandoRef.addValueEventListener(object : ValueEventListener {
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


    override fun getLocationById(id: String): Single<Location> = Single.create { emitter: SingleEmitter<Location> ->
        orlandoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = emitter.onError(Throwable("Database cancelled"))

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild(id))
                    emitter.onSuccess(p0.child(id).extractLocation())
                else
                    emitter.onError(Throwable("Location $id not found"))
            }

        })
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