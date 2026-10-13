package com.mitmarcus.dronecatalogpro.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitmarcus.dronecatalogpro.data.AuthRepository
import com.google.firebase.FirebaseException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class AuthViewModel : ViewModel() {
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            error = "Enter email and password"
            return
        }
        run(onSuccess) { AuthRepository.signIn(email.trim(), password) }
    }

    fun signUp(name: String, email: String, password: String, onSuccess: () -> Unit) {
        when {
            name.isBlank() || email.isBlank() -> error = "Enter name and email"
            password.length < 6 -> error = "Password needs at least 6 characters"
            else -> run(onSuccess) { AuthRepository.signUp(name.trim(), email.trim(), password) }
        }
    }

    private fun run(onSuccess: () -> Unit, action: suspend () -> Unit) {
        viewModelScope.launch {
            loading = true
            error = null
            try {
                // Firebase retries forever when it cannot reach the server, so give up after 15 seconds
                withTimeout(15_000) { action() }
                onSuccess()
            } catch (e: TimeoutCancellationException) {
                error = "No answer from the server. Check your connection and try again"
            } catch (e: FirebaseException) {
                error = e.message ?: "Something went wrong"
            } finally {
                loading = false
            }
        }
    }
}
