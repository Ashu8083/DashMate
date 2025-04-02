package com.appworx.dashmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(navController: NavController, orderId: String) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Booking Confirmation") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Order Confirmed!", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Your Order ID:", fontSize = 18.sp)
            Text(text = orderId, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator()
            }

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null

                    CoroutineScope(Dispatchers.IO).launch {
                        val result = bookDeliveryPerson(orderId)
                        withContext(Dispatchers.Main) {
                            isLoading = false
                            if (result.success) {
                                navController.navigate("delivery-assigned/$orderId")
                            } else {
                                errorMessage = result.message
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Book Delivery Partner")
            }
        }
    }
}

// Data class to store API response
data class ApiResponse(val success: Boolean, val message: String)

// Function to book a delivery person
fun bookDeliveryPerson(orderId: String): ApiResponse {
    return try {
        val url = URL("https://your-django-api.com/api/book-delivery-person/")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        val requestBody = JSONObject().apply {
            put("order_id", orderId)
        }.toString()

        connection.outputStream.use { it.write(requestBody.toByteArray()) }

        val responseCode = connection.responseCode
        val responseMessage = connection.responseMessage

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()
            println("API Response: $response")
            ApiResponse(true, "Delivery person booked successfully.")
        } else {
            println("Error Response Code: $responseCode, Message: $responseMessage")
            ApiResponse(false, "Failed to book delivery. Error: $responseMessage")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        ApiResponse(false, "Network error: ${e.localizedMessage}")
    }
}