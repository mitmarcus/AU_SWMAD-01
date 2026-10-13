package com.mitmarcus.dronecatalogpro.data.weather

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Open-Meteo is free and needs no API key. Wind is requested in m/s.
interface WeatherApi {
    @GET("v1/forecast")
    suspend fun forecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,wind_speed_10m,wind_gusts_10m,precipitation,weather_code",
        @Query("hourly") hourly: String = "wind_speed_10m,wind_gusts_10m,precipitation_probability",
        @Query("forecast_hours") forecastHours: Int = 12,
        @Query("wind_speed_unit") windSpeedUnit: String = "ms",
        @Query("timezone") timezone: String = "auto"
    ): ForecastResponse

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        val instance: WeatherApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(WeatherApi::class.java)
        }
    }
}
