package com.mitmarcus.dronecatalogpro.data.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Response from https://api.open-meteo.com/v1/forecast (only the fields the app asks for)
@Serializable
data class ForecastResponse(
    val current: CurrentWeather,
    val hourly: HourlyWeather
)

@Serializable
data class CurrentWeather(
    val time: String,
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("wind_speed_10m") val windSpeed: Double,
    @SerialName("wind_gusts_10m") val windGusts: Double,
    val precipitation: Double,
    @SerialName("weather_code") val weatherCode: Int
)

@Serializable
data class HourlyWeather(
    val time: List<String>,
    @SerialName("wind_speed_10m") val windSpeed: List<Double?>,
    @SerialName("wind_gusts_10m") val windGusts: List<Double?>,
    @SerialName("precipitation_probability") val precipitationProbability: List<Int?>
)
