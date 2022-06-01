package com.example.sotoontest.api.list

data class PlacesDto (
    val id: String,
    val dist: Float?,
    val poi: Poi?,
    val address: Address?
        )

data class Address (
    val country: String?,
    val streetName: String?,
    val streetNumber: String?,
    val freeformAddress: String?,
    val localName: String?
)

data class Poi (
    val name: String?,
    val phone: String?
)