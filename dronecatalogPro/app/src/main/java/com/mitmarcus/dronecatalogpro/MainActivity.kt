package com.mitmarcus.dronecatalogpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mitmarcus.dronecatalogpro.navigation.AppNavHost
import com.mitmarcus.dronecatalogpro.ui.theme.DronecatalogTheme

// Single activity. All screens are Compose Navigation destinations in AppNavHost.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DronecatalogTheme {
                AppNavHost()
            }
        }
    }
}
