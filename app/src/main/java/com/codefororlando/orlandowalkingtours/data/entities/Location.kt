package com.codefororlando.orlandowalkingtours.data.entities

/**
 * Created by ryan on 10/12/17.
 */
data class Location(val name: String,
                    val address: String,
                    val description: String,
                    val coordinates: Coordinates,
                    val pictures: List<String>,
                    val registryDates: RegistryDates)