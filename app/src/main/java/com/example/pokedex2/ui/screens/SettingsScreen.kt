package com.example.pokedex2.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pokedex2.PokemonViewModel

@Composable
fun SettingsScreen(viewModel: PokemonViewModel) {
    viewModel.getCacheStats()
    val stats = viewModel.cacheStats.observeAsState()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        // Title
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Cache Statistics Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(
                    text = "Cache Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Text(
                    text = stats.value?.toString() ?: "Loading...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Clear Cache Section
        Text(
            text = "Cache Options",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Button(
                onClick = { viewModel.clearCache(PokemonViewModel.ClearOption.ALL) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
            ) {
                Text(text = "Clear All Cache")
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = { viewModel.clearCache(PokemonViewModel.ClearOption.IMAGES) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
            ) {
                Text(text = "Clear Image Cache")
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = { viewModel.clearCache(PokemonViewModel.ClearOption.POKEMON) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
            ) {
                Text(text = "Clear Pokemon Cache")
            }
        }
    }
}