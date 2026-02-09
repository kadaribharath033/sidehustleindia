package com.example.sidehustleindia.ui.dashboard

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sidehustleindia.data.HustleRepository
import com.example.sidehustleindia.data.remote.JSearchJob
import com.example.sidehustleindia.utils.ClientHunter
import com.example.sidehustleindia.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HustleDetailScreen(hustleId: String, viewModel: ProfileViewModel, onBackClick: () -> Unit) {
    val hustle = HustleRepository.allHustles.find { it.id == hustleId }
    val userProfile by viewModel.userProfile.collectAsState()
    val liveGigs by viewModel.liveGigs.collectAsState()
    val isLoading by viewModel.isLoadingGigs.collectAsState()
    val error by viewModel.gigError.collectAsState()

    LaunchedEffect(hustleId) {
        if (hustle != null) {
            viewModel.fetchLiveGigs(skill = hustle.title, city = userProfile.city)
        }
    }

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
                // Live Gigs Section
                SectionHeader("Live Gigs in ${userProfile.city} \uD83D\uDD25")
                
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (!error.isNullOrEmpty()) {
                     val context = LocalContext.current
                     Button(
                         onClick = { ClientHunter.huntOnGoogle(context, "${hustle.title} jobs in ${userProfile.city}") },
                         colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                         modifier = Modifier.fillMaxWidth()
                     ) {
                         Text(error ?: "Error")
                     }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(liveGigs) { job ->
                            GigCard(job)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Client Hunter Section
                SectionHeader("Client Hunter (Direct Hunt) \uD83C\uDFAF")
                Text("Don't wait for jobs. Hunt them directly!", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val context = LocalContext.current
                    Button(onClick = { ClientHunter.huntOnGoogle(context, "freelance ${hustle.title} ${userProfile.city}") }) {
                        Text("Maps")
                    }
                    Button(onClick = { ClientHunter.huntOnTwitter(context, hustle.title) }) {
                        Text("Twitter")
                    }
                    Button(onClick = { ClientHunter.huntOnLinkedIn(context, hustle.title) }) {
                        Text("LinkedIn")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                
                val context = LocalContext.current
                val ttsManager = androidx.compose.runtime.remember { com.example.sidehustleindia.utils.TextToSpeechManager(context) }
                
                androidx.compose.runtime.DisposableEffect(Unit) {
                    onDispose { ttsManager.destroy() }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SectionHeader("Step-by-Step Plan")
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { ttsManager.speak("Full plan. Step 1: Learn the basics. Step 2: Create a portfolio. Step 3: Reach out to businesses.") }) {
                         Icon(Icons.Default.PlayArrow, contentDescription = "Listen to Plan", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                
                StepItem(1, "Learn the basics (YouTube/Course)", ttsManager)
                StepItem(2, "Create a portfolio (3-5 samples)", ttsManager)
                StepItem(3, "Reach out to 10 local businesses", ttsManager)
                
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
fun GigCard(job: JSearchJob) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.width(280.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(job.jobTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(job.employerName, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                   val url = job.applyLink ?: return@Button
                   val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                   context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Now")
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
fun StepItem(number: Int, text: String, ttsManager: com.example.sidehustleindia.utils.TextToSpeechManager? = null) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable { ttsManager?.speak(text) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$number.",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )
        Text(text = text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (ttsManager != null) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play Step", modifier = Modifier.size(20.dp), tint = Color.Gray)
        }
    }
}
