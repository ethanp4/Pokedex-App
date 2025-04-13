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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults.cardColors
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import com.example.pokedex2.ui.theme.TypeColors
import com.example.pokedex2.ui.theme.TypeColors.bug
import com.example.pokedex2.ui.theme.TypeColors.dark
import com.example.pokedex2.ui.theme.TypeColors.dragon
import com.example.pokedex2.ui.theme.TypeColors.fairy
import com.example.pokedex2.ui.theme.TypeColors.fighting
import com.example.pokedex2.ui.theme.TypeColors.flying
import com.example.pokedex2.ui.theme.TypeColors.ghost
import com.example.pokedex2.ui.theme.TypeColors.ground
import com.example.pokedex2.ui.theme.TypeColors.ice
import com.example.pokedex2.ui.theme.TypeColors.poison
import com.example.pokedex2.ui.theme.TypeColors.psychic
import com.example.pokedex2.ui.theme.TypeColors.rock
import com.example.pokedex2.ui.theme.TypeColors.steel
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream

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
        Button(onClick = {}) {
            Text("Test")
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
                            "hp" -> Color.Green
                            "attack" -> Color.Red
                            "defense" -> Color.Yellow
                            "speed" -> Color.Cyan
                            "special-attack" -> Color.Magenta
                            "special-defense" -> Color.Blue
                            else -> Color.Gray
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
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp)) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        val filteredList = pokemonList.value.filter {
            it.name.contains(searchQuery.trim(), ignoreCase = true)
        }

        if (pokemonList.value.isEmpty()) {
            Text("Loading...")
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


//each pokemon in the list on the main screen
@Composable
fun PokemonItem(pokemon: Pokemon, viewModel: PokemonViewModel, navController: NavHostController) {
    val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png"
    val detailsPath = viewModel.pokemonDetailsMap.value?.get(pokemon.id)
    var containerColor = MaterialTheme.colorScheme.surfaceContainer

    if (detailsPath != null) {
        val gson = Gson()
        val json = FileInputStream(detailsPath).bufferedReader().use { it.readText() }
        val details = gson.fromJson(json, PokemonDetails::class.java)
        containerColor = when (details.types[0].type.name) {
            "normal" -> TypeColors.normal
            "fire" -> TypeColors.fire
            "water" -> TypeColors.water
            "electric" -> TypeColors.electric
            "grass" -> TypeColors.grass
            "ice" -> TypeColors.ice
            "fighting" -> TypeColors.fighting
            "poison" -> TypeColors.poison
            "ground" -> TypeColors.ground
            "flying" -> TypeColors.flying
            "psychic" -> TypeColors.psychic
            "bug" -> TypeColors.bug
            "rock" -> TypeColors.rock
            "ghost" -> TypeColors.ghost
            "dragon" -> TypeColors.dragon
            "dark" -> TypeColors.dark
            "steel" -> TypeColors.steel
            "fairy" -> TypeColors.fairy
            else -> containerColor
        }
    }

//    Log.d("Details from main", "Pokemon ${pokemon.name} ${detailsPath}")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = {
            viewModel.uiState.value.selectedPokemonId = pokemon.id!!
            navController.navigate("details/${pokemon.id}")
            Log.d("Click", "${pokemon.name} was clicked")
        },
        colors = CardColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.30f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // pokemon image
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(56.dp)
                    .padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "#${pokemon.id.toString().padStart(4, '0')}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }
    }
}

