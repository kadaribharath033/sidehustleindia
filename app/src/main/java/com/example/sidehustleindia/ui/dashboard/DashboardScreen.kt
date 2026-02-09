package com.example.sidehustleindia.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.sidehustleindia.model.Hustle
import com.example.sidehustleindia.utils.AppLanguage
import com.example.sidehustleindia.utils.LanguageManager
import com.example.sidehustleindia.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ProfileViewModel,
    onHustleClick: (String) -> Unit
) {
    val hustles by viewModel.matchedHustles.collectAsState(initial = emptyList())
    val userProfile by viewModel.userProfile.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(LanguageManager.get("find_gigs"), fontWeight = FontWeight.Bold) },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Text(LanguageManager.currentLanguage.nativeName.take(2), style = MaterialTheme.typography.labelLarge)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        AppLanguage.values().forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language.nativeName) },
                                onClick = {
                                    LanguageManager.currentLanguage = language
                                    expanded = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                 Text(
                     text = "Based on your skills in ${userProfile.city}",
                     style = MaterialTheme.typography.bodyMedium,
                     color = MaterialTheme.colorScheme.onSurfaceVariant
                 )
            }
            
            if (hustles.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No hustles found yet. Try adding more skills!")
                    }
                }
            }

            items(hustles) { hustle ->
                HustleCard(hustle = hustle, onClick = { onHustleClick(hustle.id) })
            }
        }
    }
}

@Composable
fun HustleCard(hustle: Hustle, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = hustle.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = hustle.estimatedIncome,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = hustle.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                hustle.tags.take(3).forEach { tag ->
                    SuggestionChip(
                        onClick = { },
                        label = { Text(tag) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        }
    }
}
