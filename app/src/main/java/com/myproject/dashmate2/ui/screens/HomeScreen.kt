package com.appworx.dashmate2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.myproject.dashmate2.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val images = listOf(R.drawable.slide1, R.drawable.slide2, R.drawable.slide3)
    val pagerState = rememberPagerState(pageCount = { images.size })

    // Automatic slideshow change every 3 seconds
    LaunchedEffect(pagerState.currentPage) {
        while (true) {
            delay(3000) // Delay in milliseconds (3 seconds)
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ProfileSidebar(userEmail = user?.email ?: "Guest", navController = navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Home") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Slideshow
                HorizontalPager(state = pagerState, modifier = Modifier.height(300.dp)) { page ->
                    Image(
                        painter = painterResource(id = images[page]),
                        contentDescription = "Slide ${page + 1}",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Welcome message
                Text(text = "Welcome to DashMate", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                // Paragraph under the welcome message
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "DashMate offers fast and reliable delivery services, tailored to meet your needs. Book your delivery service now and experience seamless and efficient solutions.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center
                )

                // Button to navigate to book service
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { navController.navigate("book-service") }) {
                    Text("Book a Service")
                }
            }
        }
    }
}

@Composable
fun ProfileSidebar(userEmail: String, navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(Color(0xFF0033CC)), // Sidebar color
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = userEmail,
            fontSize = 16.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(thickness = 1.dp, color = Color.White)
        Spacer(modifier = Modifier.height(24.dp))

        // Add "View Profile" option
        Button(
            onClick = {
                navController.navigate("profile")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("View Profile", color = Color.White)
        }

        // Logout Button
        Button(
            onClick = {
                auth.signOut()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Logout", color = Color.White)
        }
    }
}