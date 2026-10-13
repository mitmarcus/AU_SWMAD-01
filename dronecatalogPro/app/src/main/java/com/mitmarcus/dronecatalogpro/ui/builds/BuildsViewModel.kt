package com.mitmarcus.dronecatalogpro.ui.builds

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitmarcus.dronecatalogpro.data.DroneBuild
import com.mitmarcus.dronecatalogpro.data.FirestoreRepository
import com.google.firebase.FirebaseException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BuildsViewModel : ViewModel() {

    val builds: StateFlow<List<DroneBuild>> = FirestoreRepository.buildsFlow()
        .catch { e ->
            Log.w("BuildsViewModel", "Could not load builds", e)
            error = "Could not load builds"
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var error by mutableStateOf<String?>(null)
        private set

    fun createBuild(name: String) = firestore { FirestoreRepository.createBuild(name) }

    fun addPart(buildId: String, partId: Int) = firestore { FirestoreRepository.addPart(buildId, partId) }

    fun createBuildWithPart(name: String, partId: Int, onDone: (String) -> Unit) = firestore {
        val buildId = FirestoreRepository.createBuild(name)
        FirestoreRepository.addPart(buildId, partId)
        onDone(buildId)
    }

    private fun firestore(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                error = null
                action()
            } catch (e: FirebaseException) {
                Log.w("BuildsViewModel", "Firestore write failed", e)
                error = e.message ?: "Could not save"
            }
        }
    }
}
