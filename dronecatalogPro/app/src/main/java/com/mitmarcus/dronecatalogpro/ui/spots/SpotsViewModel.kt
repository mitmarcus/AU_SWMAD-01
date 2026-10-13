package com.mitmarcus.dronecatalogpro.ui.spots

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitmarcus.dronecatalogpro.data.FirestoreRepository
import com.mitmarcus.dronecatalogpro.data.FlyingSpot
import com.google.firebase.FirebaseException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SpotsViewModel : ViewModel() {

    var error by mutableStateOf<String?>(null)
        private set

    val spots: StateFlow<List<FlyingSpot>> = FirestoreRepository.spotsFlow()
        .catch { e ->
            Log.w("SpotsViewModel", "Could not load spots", e)
            error = "Could not load flying spots"
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addSpot(name: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                FirestoreRepository.addSpot(name, latitude, longitude)
            } catch (e: FirebaseException) {
                Log.w("SpotsViewModel", "Could not save spot", e)
                error = e.message ?: "Could not save spot"
            }
        }
    }
}
