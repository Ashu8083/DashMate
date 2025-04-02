package com.appworx.dashmate2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavController, vehicleType: String?) {
    var productName by remember { mutableStateOf("") }
    var productWeight by remember { mutableStateOf("") }
    var pickupLocation by remember { mutableStateOf("") }
    var dropLocation by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Product Details - $vehicleType") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "Enter Product & Delivery Details", fontSize = 22.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = productWeight, onValueChange = { productWeight = it }, label = { Text("Product Weight (kg)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = pickupLocation, onValueChange = { pickupLocation = it }, label = { Text("Pickup Location") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = dropLocation, onValueChange = { dropLocation = it }, label = { Text("Drop Location") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null

                    CoroutineScope(Dispatchers.Main).launch {
                        isLoading = false
                        navController.navigate("confirmation/12345") // Dummy order ID for testing
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Proceed with Booking")
            }
        }
    }
}
