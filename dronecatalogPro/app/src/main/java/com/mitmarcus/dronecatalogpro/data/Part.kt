package com.mitmarcus.dronecatalogpro.data

enum class PartCategory(val title: String) {
    FRAME("Frames"),
    MOTOR("Motors"),
    ESC("ESCs"),
    FLIGHT_CONTROLLER("Flight Controllers"),
    CAMERA("Cameras"),
    VTX("Video Transmitters"),
    ANTENNA("Antennas")
}

data class Part(
    val id: Int,
    val name: String,
    val brand: String,
    val category: PartCategory,
    val price: Int,                 // euro, approximate
    val keySpec: String,            // shown in the list so parts can be compared
    val description: String,
    val specs: Map<String, String>  // all specs, shown on the detail screen
)
