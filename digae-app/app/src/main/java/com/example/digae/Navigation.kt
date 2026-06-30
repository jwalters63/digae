package com.example.digae

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.digae.ui.ViewModelFactory
import com.example.digae.ui.auth.AuthViewModel
import com.example.digae.ui.auth.LoginScreen
import com.example.digae.ui.dashboard.DashboardScreen
import com.example.digae.ui.splash.SplashScreenWrapper
import com.example.digae.ui.components.BarTransitionOverlay
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

@Serializable data object DashboardKey : NavKey

@Composable
fun MainNavigation() {
  val context = LocalContext.current
  val factory = remember { ViewModelFactory(context) }
  val backStack = rememberNavBackStack(Main)
  var isTransitioning by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize()) {
    NavDisplay(
      backStack = backStack,
      onBack = { backStack.removeLastOrNull() },
      entryProvider =
        entryProvider {
          entry<Main> {
            val viewModel: AuthViewModel = viewModel(factory = factory)
            
            SplashScreenWrapper {
                LoginScreen(
                   viewModel = viewModel,
                   onLoginSuccess = { isTransitioning = true }
                )
            }
          }
          entry<DashboardKey> {
              val dashboardViewModel: com.example.digae.ui.dashboard.DashboardViewModel = viewModel(factory = factory)
              DashboardScreen(
                  viewModel = dashboardViewModel,
                  onLogoutClick = { 
                      dashboardViewModel.logout()
                      backStack.removeLastOrNull() 
                  }
              )
          }
        },
    )

    BarTransitionOverlay(
        isVisible = isTransitioning,
        onCoverScreen = {
            backStack.add(DashboardKey)
        },
        onTransitionFinished = {
            isTransitioning = false
        }
    )
  }
}
