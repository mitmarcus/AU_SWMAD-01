package com.mitmarcus.dronecatalogpro.ui.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SignUpScreen(
    onSignedUp: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    AuthForm(title = "Create account", error = viewModel.error) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        EmailField(email) { email = it }
        PasswordField(password) { password = it }
        AuthButton("Create account", loading = viewModel.loading) {
            viewModel.signUp(name, email, password, onSignedUp)
        }
        TextButton(onClick = onBack) { Text("I already have an account") }
    }
}
