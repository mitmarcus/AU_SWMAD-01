package com.mitmarcus.dronecatalogpro.ui.builds

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mitmarcus.dronecatalogpro.data.DroneBuild
import com.mitmarcus.dronecatalogpro.data.FirestoreRepository
import com.mitmarcus.dronecatalogpro.navigation.BuildDetail
import com.mitmarcus.dronecatalogpro.ui.components.Loadable
import com.mitmarcus.dronecatalogpro.ui.components.asLoadable
import com.google.firebase.FirebaseException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BuildDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val buildId = savedStateHandle.toRoute<BuildDetail>().buildId

    var error by mutableStateOf<String?>(null)
        private set

    val build: StateFlow<Loadable<DroneBuild>> = FirestoreRepository.buildFlow(buildId)
        .asLoadable()
        .catch { e ->
            Log.w("BuildDetailViewModel", "Could not load build", e)
            error = "Could not load build"
            emit(Loadable.Missing)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Loadable.Loading)

    fun addOne(partId: Int) = firestore { FirestoreRepository.addPart(buildId, partId) }

    fun removeOne(partId: Int, currentQuantity: Int) =
        firestore { FirestoreRepository.removePart(buildId, partId, currentQuantity) }

    // viewModelScope runs on Main.immediate, so the delete is sent to Firestore
    // before onDeleted() pops this screen and clears the ViewModel
    fun delete(onDeleted: () -> Unit) {
        firestore { FirestoreRepository.deleteBuild(buildId) }
        onDeleted()
    }

    private fun firestore(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
            } catch (e: FirebaseException) {
                Log.w("BuildDetailViewModel", "Firestore write failed", e)
                error = e.message ?: "Could not save"
            }
        }
    }
}
