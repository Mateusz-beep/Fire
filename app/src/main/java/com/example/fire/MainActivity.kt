package com.example.fire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.foundation.text.BasicTextField


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val database = FirebaseDatabase.getInstance().reference

    NavHost(navController = navController, startDestination = "start_screen") {
        composable("start_screen") {
            StartScreen(navController)
        }
        composable("firebase_screen") {
            FirebaseAppUI(database)
        }
    }
}

@Composable
fun StartScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Wykonał: Mateusz Nowak",
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tytuł: Firebase",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("firebase_screen") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F684D))
        ) {
            Text(text = "Przejdź do Firebase")
        }
    }
}

@Composable
fun FirebaseAppUI(database: DatabaseReference) {
    var inputData by remember { mutableStateOf("") }
    var outputData by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Podaj tekst do wysłania:")
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = inputData,
            onValueChange = { inputData = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textStyle = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (inputData.isNotBlank()) {
                    sendDataToFirebase(database, inputData)
                    inputData = "" // Clear input after sending
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F684D))

        ) {
            Text("Wyślij dane")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                readDataFromFirebase(database) { data ->
                    outputData = data ?: "No data available"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F684D))
        ) {
            Text("Odczytaj dane")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Baza danych pokazuje: \n "+"$outputData",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun sendDataToFirebase(database: DatabaseReference, data: String) {
    database.child("words").push().setValue(data)
}

fun readDataFromFirebase(database: DatabaseReference, onDataRetrieved: (String?) -> Unit) {
    database.child("words").get().addOnSuccessListener { snapshot ->
        if (snapshot.exists()) {
            val messages = snapshot.children.joinToString("\n") { it.value.toString() }
            onDataRetrieved(messages)
        } else {
            onDataRetrieved(null)
        }
    }
}
