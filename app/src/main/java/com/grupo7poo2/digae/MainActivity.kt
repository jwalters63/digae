package com.grupo7poo2.digae

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grupo7poo2.digae.ui.CriticidadDetalleScreen
import com.grupo7poo2.digae.ui.CriticidadScreen
import com.grupo7poo2.digae.ui.CriticidadViewModel
import com.grupo7poo2.digae.ui.HomeDashboardScreen
import com.grupo7poo2.digae.ui.theme.DIGAETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DIGAETheme {
                val navController = rememberNavController()

                // ViewModel elevado al scope de la Activity para que sea
                // compartido por todas las pantallas del grafo de navegación.
                val criticidadViewModel: CriticidadViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "dashboard"
                ) {
                    composable("dashboard") {
                        HomeDashboardScreen(
                            onModulo1Click = { navController.navigate("criticidad") }
                        )
                    }
                    composable("criticidad") {
                        CriticidadScreen(
                            navController = navController,
                            viewModel = criticidadViewModel
                        )
                    }
                    composable("criticidad/{matrizId}") { backStackEntry ->
                        val matrizId = backStackEntry.arguments?.getString("matrizId") ?: return@composable
                        CriticidadDetalleScreen(
                            matrizId = matrizId,
                            navController = navController,
                            viewModel = criticidadViewModel
                        )
                    }
                }
            }
        }
    }
}