package com.mitmarcus.dronecatalogpro.ui.components

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// State of one Firestore document on a detail screen
sealed interface Loadable<out T> {
    data object Loading : Loadable<Nothing>
    data object Missing : Loadable<Nothing>
    data class Ready<T>(val value: T) : Loadable<T>
}

fun <T : Any> Flow<T?>.asLoadable(): Flow<Loadable<T>> =
    map { if (it == null) Loadable.Missing else Loadable.Ready(it) }
