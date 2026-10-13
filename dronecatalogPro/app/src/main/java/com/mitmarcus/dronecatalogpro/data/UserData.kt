package com.mitmarcus.dronecatalogpro.data

// A drone build saved in Firestore. parts maps part id -> quantity (a quad needs 4 motors)
data class DroneBuild(
    val id: String,
    val name: String,
    val parts: Map<Int, Int>
) {
    val partCount: Int get() = parts.values.sum()

    val totalPrice: Int
        get() = parts.entries.sumOf { (partId, quantity) ->
            (PartRepository.getPartById(partId)?.price ?: 0) * quantity
        }
}

// A place to fly, saved from the phone's GPS position
data class FlyingSpot(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double
)
