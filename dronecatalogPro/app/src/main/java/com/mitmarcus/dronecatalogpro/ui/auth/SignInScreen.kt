package com.mitmarcus.dronecatalogpro.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SignInScreen(
    onSignedIn: () -> Unit,
    onCreateAccount: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    AuthForm(title = "Sign in", error = viewModel.error) {
        EmailField(email) { email = it }
        PasswordField(password) { password = it }
        AuthButton("Sign in", loading = viewModel.loading) {
            viewModel.signIn(email, password, onSignedIn)
        }
        TextButton(onClick = onCreateAccount) { Text("No account? Create one") }
    }
}

// Shared layout for the sign-in and sign-up screens.
// The Box centers the form, and the form scrolls when the keyboard leaves too little room.
@Composable
fun AuthForm(title: String, error: String?, content: @Composable () -> Unit) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Handyman,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp)
                )
                Text("FPV Parts Pro", style = MaterialTheme.typography.headlineMedium)
                Text(title, style = MaterialTheme.typography.titleMedium)
                content()
                if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// Submit button that shows the spinner in place of its text, so the form does not jump
@Composable
fun AuthButton(text: String, loading: Boolean, onClick: () -> Unit) {
    val focusManager = LocalFocusManager.current
    Button(
        onClick = {
            focusManager.clearFocus() // closes the keyboard
            onClick()
        },
        enabled = !loading,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        } else {
            Text(text)
        }
    }
}

@Composable
fun EmailField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Email") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Password") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth()
    )
}
