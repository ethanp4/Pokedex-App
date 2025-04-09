package com.example.pokedex2.ui

import com.example.pokedex2.PokemonViewModel
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokedex2.R
import com.example.pokedex2.data.Pokemon
import com.example.pokedex2.data.PokemonDetails
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.LinearProgressIndicator

import androidx.lifecycle.viewmodel.compose.viewModel

//stores the names of each screen for navigation
enum class PokedexMainScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Details(title = R.string.details)
}

//main composable for the app
@Composable
fun PokedexApp(
    viewModel: PokemonViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        modifier = Modifier
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = PokedexMainScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = PokedexMainScreen.Start.name) {
                PokemonList(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding),
                    navController = navController
                )
            }
            composable(route = "details/{pokemonId}") { backStackEntry ->
                val pokemonId = backStackEntry.arguments?.getString("pokemonId")?.toIntOrNull()
                pokemonId?.let {
                    PokemonDetailsScreen(pokemonId = it, viewModel = viewModel)
                }
            }
        }
    }
}

//this composable is the details screen
@Composable
fun PokemonDetailsScreen(pokemonId: Int, viewModel: PokemonViewModel) {
    viewModel.getPokemonById(pokemonId)
    val pokemon = viewModel.currentPokemonDetails.observeAsState()

    if (pokemon.value == null) {
        Text("Loading...")
    } else {
        val details = pokemon.value!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(details.sprites.other?.officialArtwork?.frontDefault),
                contentDescription = details.name,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "#${details.id} ${details.name.replaceFirstChar { it.uppercase() }}")
            Spacer(modifier = Modifier.height(8.dp))

            // types
            Text(
                text = "Types: ${details.types.joinToString { it.type.name.replaceFirstChar { it.uppercase() } }}"
            )

            // abilities
            Text(
                text = "Abilities: ${details.abilities.joinToString { it.ability.name.replaceFirstChar { it.uppercase() } }}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Base Stats
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(text = "Base Stats:", modifier = Modifier.padding(bottom = 8.dp))

                details.stats.forEach { stat ->
                    val statName = stat.stat.name.replace("-", " ").replaceFirstChar { it.uppercase() }
                    val statValue = stat.base_stat
                    val progress = (statValue.coerceAtMost(150)) / 150f

                    Text(text = "$statName: $statValue", modifier = Modifier.padding(vertical = 4.dp))

                    //Bars under each stat
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .padding(bottom = 8.dp),
                        color = when (stat.stat.name) {
                            "hp" -> androidx.compose.ui.graphics.Color.Green
                            "attack" -> androidx.compose.ui.graphics.Color.Red
                            "defense" -> androidx.compose.ui.graphics.Color.Yellow
                            "speed" -> androidx.compose.ui.graphics.Color.Cyan
                            "special-attack" -> androidx.compose.ui.graphics.Color.Magenta
                            "special-defense" -> androidx.compose.ui.graphics.Color.Blue
                            else -> androidx.compose.ui.graphics.Color.Gray
                        }
                    )
                }
            }
        }
    }
}

//this composable displays the list of every pokemon
@Composable
fun PokemonList(
    viewModel: PokemonViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val pokemonList = viewModel.pokemonList.observeAsState(initial = emptyList())
    if (pokemonList.value.isEmpty()) {
        Text("Loading..")
    } else {
        LazyColumn(modifier = modifier) {
            items(pokemonList.value) {
                PokemonItem(pokemon = it, viewModel = viewModel, navController = navController)
            }
        }
    }
}

//each pokemon in the list on the main screen
@Composable
fun PokemonItem(pokemon: Pokemon, viewModel: PokemonViewModel, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = {
            viewModel.uiState.value.selectedPokemonId = pokemon.id!!
            navController.navigate("details/${pokemon.id}")
            Log.d("Click", "${pokemon.name} was clicked")
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {

            // Name and ID
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "#${pokemon.id.toString().padStart(4, '0')}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
            }

        }
    }
}
