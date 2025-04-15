package com.example.pokedex2.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Settings")
        Text(text = "Cache Statistics")
        Text(text = stats.value.toString())
        Text(text = "Clear Cache")
        Row() {
            Button(onClick = { viewModel.clearCache(PokemonViewModel.ClearOption.ALL)}) {
                Text(text = "All")
            }
            Button(onClick = { viewModel.clearCache(PokemonViewModel.ClearOption.IMAGES)}) {
                Text(text = "Images")
            }
            Button(onClick = { viewModel.clearCache(PokemonViewModel.ClearOption.POKEMON)}) {
                Text(text = "Pokemon")
            }
        }
    }
}