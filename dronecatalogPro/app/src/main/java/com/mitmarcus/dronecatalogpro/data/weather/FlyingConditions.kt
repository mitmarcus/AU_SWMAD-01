package com.mitmarcus.dronecatalogpro.data.weather

private const val MAX_WIND_GOOD = 7.0
private const val MAX_GUSTS_GOOD = 10.0
private const val MAX_GUSTS_MARGINAL = 14.0
private const val MAX_RAIN_CHANCE = 30

enum class FlyVerdict(val title: String, val advice: String) {
    GOOD("Good to fly", "Light wind and no rain."),
    MARGINAL("Fly with care", "Strong wind or gusts. Expect drift and a shorter flight time."),
    NO_FLY("Stay on the ground", "Rain, fog, storm or gusts above 14 m/s.")
}

data class HourForecast(
    val time: String,          // "14:00"
    val windSpeed: Double,
    val windGusts: Double,
    val rainChance: Int
)

data class FlyingConditions(
    val current: CurrentWeather,
    val verdict: FlyVerdict,
    val hours: List<HourForecast>,
    val bestHour: HourForecast?    // calmest hour with a low chance of rain
)

fun ForecastResponse.toFlyingConditions(): FlyingConditions {
    val hours = hourly.time.indices.map { i ->
        HourForecast(
            time = hourly.time[i].substringAfter('T'),
            windSpeed = hourly.windSpeed.getOrNull(i) ?: 0.0,
            windGusts = hourly.windGusts.getOrNull(i) ?: 0.0,
            rainChance = hourly.precipitationProbability.getOrNull(i) ?: 0
        )
    }
    val bestHour = hours
        .filter { it.rainChance < MAX_RAIN_CHANCE && it.windGusts < MAX_GUSTS_MARGINAL }
        .minByOrNull { it.windGusts }

    return FlyingConditions(
        current = current,
        verdict = verdictFor(current.windSpeed, current.windGusts, current.precipitation, current.weatherCode),
        hours = hours,
        bestHour = bestHour
    )
}

fun verdictFor(windSpeed: Double, windGusts: Double, precipitation: Double, weatherCode: Int): FlyVerdict =
    when {
        precipitation > 0.0 || isBadWeather(weatherCode) || windGusts >= MAX_GUSTS_MARGINAL -> FlyVerdict.NO_FLY
        windSpeed >= MAX_WIND_GOOD || windGusts >= MAX_GUSTS_GOOD -> FlyVerdict.MARGINAL
        else -> FlyVerdict.GOOD
    }

// WMO weather codes: 45-48 fog, 51-67 drizzle and rain, 71-77 snow, 80-86 showers, 95-99 thunderstorm
private fun isBadWeather(code: Int) = code >= 45

fun weatherDescription(code: Int): String = when (code) {
    0 -> "Clear sky"
    1, 2 -> "Partly cloudy"
    3 -> "Overcast"
    45, 48 -> "Fog"
    in 51..57 -> "Drizzle"
    in 61..67 -> "Rain"
    in 71..77 -> "Snow"
    in 80..86 -> "Showers"
    in 95..99 -> "Thunderstorm"
    else -> "Unknown"
}
