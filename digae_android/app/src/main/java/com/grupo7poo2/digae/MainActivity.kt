package com.grupo7poo2.digae

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import kotlinx.coroutines.launch
import com.grupo7poo2.digae.ui.AlertasScreen
import com.grupo7poo2.digae.ui.AlertasScreen
import com.grupo7poo2.digae.ui.BusquedaScreen
import com.grupo7poo2.digae.ui.CriticidadDetalleScreen
import com.grupo7poo2.digae.ui.CriticidadScreen
import com.grupo7poo2.digae.ui.CriticidadViewModel
import com.grupo7poo2.digae.ui.HomeDashboardScreen
import com.grupo7poo2.digae.ui.SupervisionDetalleScreen
import com.grupo7poo2.digae.ui.SupervisionScreen
import com.grupo7poo2.digae.ui.SupervisionViewModel
import com.grupo7poo2.digae.ui.TrazabilidadDetalleScreen
import com.grupo7poo2.digae.ui.TrazabilidadScreen
import com.grupo7poo2.digae.ui.TrazabilidadViewModel
import com.grupo7poo2.digae.ui.LoginScreen
import com.grupo7poo2.digae.ui.theme.DIGAETheme
import com.grupo7poo2.digae.network.auth.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            android.util.Log.e("CRASH_DIGAE", "==============================")
            android.util.Log.e("CRASH_DIGAE", "FATAL CRASH DETECTED!")
            android.util.Log.e("CRASH_DIGAE", "Thread: ${thread.name}")
            android.util.Log.e("CRASH_DIGAE", "Exception: ", exception)
            android.util.Log.e("CRASH_DIGAE", "==============================")
            
            val stackTrace = android.util.Log.getStackTraceString(exception)
            val prefs = getSharedPreferences("crash_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().putString("last_crash", stackTrace).commit()
            
            // Pass to default handler so OS can record it
            defaultExceptionHandler?.uncaughtException(thread, exception)
        }
        
        com.grupo7poo2.digae.network.RetrofitClient.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            DIGAETheme {
                val navController = rememberNavController()

                val criticidadViewModel:  CriticidadViewModel  = viewModel()
                val supervisionViewModel: SupervisionViewModel  = viewModel()
                val trazabilidadViewModel: TrazabilidadViewModel = viewModel()

                val criticidadState by criticidadViewModel.uiState.collectAsStateWithLifecycle()
                val supervisionState by supervisionViewModel.uiState.collectAsStateWithLifecycle()
                val trazabilidadState by trazabilidadViewModel.uiState.collectAsStateWithLifecycle()

                val sessionManager = SessionManager(applicationContext)
                val isUserLoggedIn = sessionManager.fetchAuthToken() != null
                
                var crashLog by androidx.compose.runtime.remember { mutableStateOf<String?>(null) }
                
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    val prefs = getSharedPreferences("crash_prefs", android.content.Context.MODE_PRIVATE)
                    val savedCrash = prefs.getString("last_crash", null)
                    if (savedCrash != null) {
                        crashLog = savedCrash
                        prefs.edit().remove("last_crash").apply()
                    }
                }
                
                if (crashLog != null) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { crashLog = null },
                        title = { androidx.compose.material3.Text("App Recovered from Crash") },
                        text = { 
                            androidx.compose.foundation.layout.Column(
                                modifier = androidx.compose.ui.Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())
                            ) {
                                androidx.compose.material3.Text("The app crashed previously. Here is the log to report to the developer:\n\n$crashLog", style = androidx.compose.ui.text.TextStyle(fontSize = 10.sp))
                            }
                        },
                        confirmButton = {
                            androidx.compose.material3.TextButton(onClick = { crashLog = null }) { 
                                androidx.compose.material3.Text("Close") 
                            }
                        }
                    )
                }


                val userName = sessionManager.fetchUserName() ?: "Usuario"
                val userEmail = sessionManager.fetchUserEmail() ?: ""
                val userInitials = if (userName.isNotBlank()) userName.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").take(2) else "US"

                // DRAWER: AnimatedVisibility Overlay (no extra window)
                androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = if (isUserLoggedIn) "dashboard" else "login"
                    ) {

                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("dashboard") {
                            HomeDashboardScreen(
                                sessionManager = sessionManager,
                                matricesCount = criticidadState.matrices.size,
                                auditoriasCount = supervisionState.supervisiones.size,
                                bitacorasCount = trazabilidadState.bitacoras.size,
                                onModulo1Click = { navController.navigate("criticidad") },
                                onModulo2Click = { navController.navigate("supervision") },
                                onModulo3Click = { navController.navigate("trazabilidad") },
                                onNavigateToSearch = { navController.navigate("buscar") {
                                    popUpTo("dashboard") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                } },
                                onNavigateToAlertas = { navController.navigate("alertas") {
                                    popUpTo("dashboard") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                } },
                                onNavigateToInstalaciones = {
                                    navController.navigate("instalaciones") {
                                        launchSingleTop = true
                                    }
                                },
                                onLogout = {
                                    sessionManager.clearSession()
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.id) { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable("instalaciones") {
                            com.grupo7poo2.digae.ui.InstalacionesScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }

                    composable("buscar") {
                        BusquedaScreen(
                            criticidadViewModel = criticidadViewModel,
                            supervisionViewModel = supervisionViewModel,
                            trazabilidadViewModel = trazabilidadViewModel,
                            onNavigateToHome = { navController.navigate("dashboard") {
                                popUpTo("dashboard") { inclusive = false }
                                launchSingleTop = true
                            } },
                            onNavigateToInstalaciones = { navController.navigate("instalaciones") {
                                launchSingleTop = true
                            } },
                            onNavigateToAlertas = { navController.navigate("alertas") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            } },
                            onMatrizClick = { id -> navController.navigate("criticidad/$id") },
                            onSupervisionClick = { id -> navController.navigate("supervision/$id") },
                            onBitacoraClick = { id -> navController.navigate("trazabilidad/$id") }
                        )
                    }

                    composable("alertas") {
                        AlertasScreen(
                            criticidadViewModel = criticidadViewModel,
                            supervisionViewModel = supervisionViewModel,
                            trazabilidadViewModel = trazabilidadViewModel,
                            onNavigateToHome = { navController.navigate("dashboard") {
                                popUpTo("dashboard") { inclusive = false }
                                launchSingleTop = true
                            } },
                            onNavigateToSearch = { navController.navigate("buscar") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            } },
                            onNavigateToInstalaciones = { navController.navigate("instalaciones") {
                                launchSingleTop = true
                            } },
                            onMatrizClick = { id -> navController.navigate("criticidad/$id") },
                            onSupervisionClick = { id -> navController.navigate("supervision/$id") },
                            onBitacoraClick = { id -> navController.navigate("trazabilidad/$id") }
                        )
                    }

                    composable("criticidad") {
                        CriticidadScreen(navController = navController, viewModel = criticidadViewModel)
                    }
                    composable("criticidad/{matrizId}") { backStackEntry ->
                        val matrizId = backStackEntry.arguments?.getString("matrizId") ?: return@composable
                        CriticidadDetalleScreen(matrizId = matrizId, navController = navController, viewModel = criticidadViewModel)
                    }

                    composable("supervision") {
                        SupervisionScreen(navController = navController, viewModel = supervisionViewModel)
                    }
                    composable("supervision/{supervisionId}") { backStackEntry ->
                        val supId = backStackEntry.arguments?.getString("supervisionId") ?: return@composable
                        SupervisionDetalleScreen(supervisionId = supId, navController = navController, viewModel = supervisionViewModel)
                    }

                    composable("trazabilidad") {
                        TrazabilidadScreen(navController = navController, viewModel = trazabilidadViewModel)
                    }
                    composable("trazabilidad/{bitacoraId}") { backStackEntry ->
                        val bitId = backStackEntry.arguments?.getString("bitacoraId") ?: return@composable
                        TrazabilidadDetalleScreen(bitacoraId = bitId, navController = navController, viewModel = trazabilidadViewModel)
                    }
                } // End of NavHost
                } // End of Box wrapping NavHost
            }
        }
    }
}
