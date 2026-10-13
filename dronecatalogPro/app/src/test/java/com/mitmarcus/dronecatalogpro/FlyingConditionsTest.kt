package com.mitmarcus.dronecatalogpro

import com.mitmarcus.dronecatalogpro.data.weather.FlyVerdict
import com.mitmarcus.dronecatalogpro.data.weather.ForecastResponse
import com.mitmarcus.dronecatalogpro.data.weather.toFlyingConditions
import com.mitmarcus.dronecatalogpro.data.weather.verdictFor
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class FlyingConditionsTest {

    // Shortened copy of a real Open-Meteo response for Aarhus
    private val sample = """
        {
          "latitude": 56.16, "longitude": 10.2, "timezone": "Europe/Copenhagen",
          "current": {
            "time": "2026-09-29T20:45", "interval": 900, "temperature_2m": 16.2,
            "wind_speed_10m": 5.7, "wind_gusts_10m": 9.1, "precipitation": 0.0, "weather_code": 3
          },
          "hourly": {
            "time": ["2026-09-29T20:00", "2026-09-29T21:00", "2026-09-29T22:00"],
            "wind_speed_10m": [4.8, 6.0, 6.2],
            "wind_gusts_10m": [7.8, 9.5, 10.9],
            "precipitation_probability": [0, 40, null]
          }
        }
    """.trimIndent()

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesForecastAndPicksCalmestDryHour() {
        val conditions = json.decodeFromString<ForecastResponse>(sample).toFlyingConditions()

        assertEquals(FlyVerdict.GOOD, conditions.verdict)
        assertEquals(3, conditions.hours.size)
        assertEquals("20:00", conditions.bestHour?.time)
        assertEquals(0, conditions.hours[2].rainChance)
    }

    @Test
    fun verdictLimits() {
        assertEquals(FlyVerdict.GOOD, verdictFor(3.0, 6.0, 0.0, 0))
        assertEquals(FlyVerdict.MARGINAL, verdictFor(7.5, 9.0, 0.0, 1))
        assertEquals(FlyVerdict.MARGINAL, verdictFor(4.0, 11.0, 0.0, 2))
        assertEquals(FlyVerdict.NO_FLY, verdictFor(4.0, 14.0, 0.0, 0))
        assertEquals(FlyVerdict.NO_FLY, verdictFor(2.0, 3.0, 0.4, 61))
        assertEquals(FlyVerdict.NO_FLY, verdictFor(1.0, 2.0, 0.0, 45))
    }
}
