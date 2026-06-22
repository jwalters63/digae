package com.grupo7poo2.digae

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.grupo7poo2.digae.ui.HomeDashboardScreen
import com.grupo7poo2.digae.ui.theme.DIGAETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DIGAETheme {
                HomeDashboardScreen()
            }
        }
    }
}