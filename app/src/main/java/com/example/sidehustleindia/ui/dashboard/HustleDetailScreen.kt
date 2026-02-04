package com.example.sidehustleindia.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sidehustleindia.data.HustleRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HustleDetailScreen(hustleId: String, onBackClick: () -> Unit) {
    val hustle = HustleRepository.allHustles.find { it.id == hustleId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(hustle?.title ?: "Hustle Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (hustle == null) {
            Box(modifier = Modifier.padding(padding)) {
                Text("Hustle not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = hustle.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Estimated Income: ${hustle.estimatedIncome}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                SectionHeader("Description")
                Text(text = hustle.description, style = MaterialTheme.typography.bodyLarge)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SectionHeader("Step-by-Step Plan")
                // Placeholder plan for now
                StepItem(1, "Learn the basics (YouTube/Course)")
                StepItem(2, "Create a portfolio (3-5 samples)")
                StepItem(3, "Reach out to 10 local businesses")
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SectionHeader("Pricing Guidance")
                Text("Entry Level: ₹500 - ₹1000 per unit", style = MaterialTheme.typography.bodyMedium)
                Text("Experienced: ₹2000+ per unit", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(24.dp))
                
                Button(onClick = { /* TODO: Open Templates */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("View Client Message Templates")
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun StepItem(number: Int, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$number.",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}
