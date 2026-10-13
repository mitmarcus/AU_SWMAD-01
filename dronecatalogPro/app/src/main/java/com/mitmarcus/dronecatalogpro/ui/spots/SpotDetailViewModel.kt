package com.mitmarcus.dronecatalogpro.ui.spots

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mitmarcus.dronecatalogpro.data.FirestoreRepository
import com.mitmarcus.dronecatalogpro.data.FlyingSpot
import com.mitmarcus.dronecatalogpro.data.weather.FlyingConditions
import com.mitmarcus.dronecatalogpro.data.weather.WeatherApi
import com.mitmarcus.dronecatalogpro.data.weather.toFlyingConditions
import com.mitmarcus.dronecatalogpro.navigation.SpotDetail
import com.mitmarcus.dronecatalogpro.ui.components.Loadable
import com.mitmarcus.dronecatalogpro.ui.components.asLoadable
import com.google.firebase.FirebaseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface WeatherState {
    data object Loading : WeatherState
    data class Error(val message: String) : WeatherState
    data class Ready(val conditions: FlyingConditions) : WeatherState
}

class SpotDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val spotId = savedStateHandle.toRoute<SpotDetail>().spotId
    private val spotFlow = FirestoreRepository.spotFlow(spotId)

    val spot: StateFlow<Loadable<FlyingSpot>> = spotFlow
        .asLoadable()
        .catch { e ->
            Log.w("SpotDetailViewModel", "Could not load spot", e)
            emit(Loadable.Missing)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Loadable.Loading)

    var weather by mutableStateOf<WeatherState>(WeatherState.Loading)
        private set

    init {
        // Fetch the forecast once the spot's coordinates arrive from Firestore
        viewModelScope.launch {
            try {
                loadWeather(spotFlow.filterNotNull().first())
            } catch (e: FirebaseException) {
                weather = WeatherState.Error("Could not load spot")
            }
        }
    }

    fun refresh() {
        val current = spot.value as? Loadable.Ready ?: return
        viewModelScope.launch { loadWeather(current.value) }
    }

    private suspend fun loadWeather(spot: FlyingSpot) {
        weather = WeatherState.Loading
        weather = try {
            val forecast = WeatherApi.instance.forecast(spot.latitude, spot.longitude)
            WeatherState.Ready(forecast.toFlyingConditions())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // IOException (no network), HttpException (bad status) or SerializationException
            Log.w("SpotDetailViewModel", "Weather request failed", e)
            WeatherState.Error("Could not get the forecast. Check your connection.")
        }
    }

    // See BuildDetailViewModel.delete: the Firestore call starts before the screen closes
    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                FirestoreRepository.deleteSpot(spotId)
            } catch (e: FirebaseException) {
                Log.w("SpotDetailViewModel", "Could not delete spot", e)
            }
        }
        onDeleted()
    }
}
