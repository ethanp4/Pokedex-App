package com.example.pokedex2.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pokedex2.PokemonViewModel
import com.example.pokedex2.ui.PokemonItem

@Composable
fun FavouritesScreen(
    viewModel: PokemonViewModel,
    navController: NavHostController
) {
    val pokemonList = viewModel.pokemonList.observeAsState(initial = emptyList())
    val favorites = viewModel.favorites.observeAsState(initial = emptySet())
    var searchQuery by remember { mutableStateOf("") }

    // Get favorite Pokemon IDs
    viewModel.getFavouritePokemon()

    Column(modifier = Modifier.padding(16.dp)) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by name or ID") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        val query = searchQuery.trim().lowercase()
        val filteredList = pokemonList.value.filter { pokemon ->
            // Only include Pokemon that are in favorites
            favorites.value.contains(pokemon.id) &&
            // And match the search query
            (pokemon.name.lowercase().contains(query) || pokemon.id?.toString() == query)
        }

        if (filteredList.isEmpty()) {
            Text("No favorite Pokemon found")
        } else {
            LazyColumn {
                items(filteredList) { pokemon ->
                    PokemonItem(
                        pokemon = pokemon,
                        viewModel = viewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}