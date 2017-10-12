package com.codefororlando.orlandowalkingtours.entities

/**
 * Created by ryan on 10/12/17.
 */
data class Location(val name: String,
                    val address: String,
                    val coordinates: Coordinates,
                    val pictures: List<String>,
                    val registryDates: RegistryDates)