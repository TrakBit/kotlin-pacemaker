package models

import java.util.*

data class Location(
    val latitude: Double= 0.0,
    val longitude: Double= 0.0,
    val id: String = UUID.randomUUID().toString())

