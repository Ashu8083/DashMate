package com.myproject.dashmate2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appworx.dashmate2.ui.screens.BookServiceScreen
import com.appworx.dashmate2.ui.screens.LoginScreen
import com.appworx.dashmate2.ui.screens.SignUpScreen
import com.appworx.dashmate2.ui.screens.ProductDetailScreen
import com.appworx.dashmate2.ui.screens.ConfirmationScreen
import com.appworx.dashmate2.ui.screens.HomeScreen
import com.appworx.dashmate2.ui.screens.UserProfileScreen

import com.google.firebase.FirebaseApp.initializeApp
import com.google.firebase.messaging.FirebaseMessaging
import com.myproject.dashmate2.ui.theme.DashMateTheme
import com.myproject.dashmate2.viewmodel.FirebaseAuthService
import com.myproject.dashmate2.viewmodel.AuthViewModel
import com.myproject.dashmate2.ui.theme.DashMateTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var authService: FirebaseAuthService

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        initializeApp(this)
        authService = FirebaseAuthService()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM Token", token ?: "No Token")
            // TODO: Send this token to your backend
        }

        enableEdgeToEdge()
        setContent {
            DashMateTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MyApp(this.authService, innerPadding)
                }
            }
        }
    }
}



@Composable
fun MyApp(authService: FirebaseAuthService, innerPadding: PaddingValues) {
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel(authService) }

    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController, authViewModel) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController, authViewModel) }
        composable("book-service") {BookServiceScreen(navController = navController) }
        composable("home") { HomeScreen(navController) }
        composable("profile") { UserProfileScreen(navController, userEmail = "user@example.com", userName = "John Doe")}


            composable("product-detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(navController, productId)
        }

        composable("confirmation/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            ConfirmationScreen(navController, orderId)
        }

        // Add Delivery Assigned Screen

    }
}




@Composable
fun SplashScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    LaunchedEffect(Unit) {
        if (authViewModel.isUserLoggedIn()) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    class MainActivity : AppCompatActivity() {
        private val authViewModel: AuthViewModel by viewModels()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                // Your Composable UI here, e.g., SignUpScreen
            }
        }
    }
}

