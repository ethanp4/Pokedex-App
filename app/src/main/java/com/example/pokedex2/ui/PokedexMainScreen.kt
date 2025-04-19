package com.example.pokedex2.ui

import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.pokedex2.PokemonViewModel
import com.example.pokedex2.R
import com.example.pokedex2.data.Pokemon
import com.example.pokedex2.ui.screens.FavouritesScreen
import com.example.pokedex2.ui.screens.SettingsScreen


//stores the names of each screen for navigation
val roundedIcons = Icons.Rounded
val outlinedIcons = Icons.Outlined
enum class PokedexMainScreen(@StringRes val title: Int, val icon: ImageVector?) {
    Home(title = R.string.app_name, roundedIcons.Home),
    Favourites(title = R.string.favourites, outlinedIcons.Star),
    Settings(title = R.string.settings, roundedIcons.Settings),
    Details(title = R.string.details, null)
}

//main composable for the app
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PokedexApp(
    viewModel: PokemonViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    isOnline: Boolean
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(
        modifier = Modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.pokedexicon),
                            contentDescription = "PokeDex Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 8.dp)
                        )
                        Text(text = "PokeDex")
                    }
                }
            )
        },

        bottomBar = {
            BottomAppBar {
                val navItems = listOf(
                    PokedexMainScreen.Home,
                    PokedexMainScreen.Favourites,
                    PokedexMainScreen.Settings
                )

                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            if (screen.icon != null) {
                                Image(
                                    imageVector = screen.icon,
                                    contentDescription = screen.name
                                )
                            }
                        },
                        label = { Text(screen.name) },
                        selected = currentRoute == screen.name,
                        onClick = {
                            navController.navigate(screen.name) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                restoreState = true
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = PokedexMainScreen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = PokedexMainScreen.Home.name) {
                PokemonList(
                    viewModel = viewModel,
                    navController = navController,
                    isOnline = isOnline
                )
            }
            composable(route = PokedexMainScreen.Favourites.name) {
                FavouritesScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable(route = "details/{pokemonId}") { backStackEntry ->
                val pokemonId = backStackEntry.arguments?.getString("pokemonId")?.toIntOrNull()
                pokemonId?.let {
                    PokemonDetailsScreen(pokemonId = it, viewModel = viewModel)
                }
            }
            composable(route = PokedexMainScreen.Settings.name) { backStackEntry ->
                SettingsScreen(viewModel)
            }
        }
    }
}

//this composable is the details screen
@Composable
fun PokemonDetailsScreen(pokemonId: Int, viewModel: PokemonViewModel) {
    viewModel.getPokemonById(pokemonId)
    val pokemon = viewModel.currentPokemonDetails.observeAsState()

    var showShiny by remember { mutableStateOf(false) }

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
            // Toggle for shiny version
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Shiny")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = showShiny,
                    onCheckedChange = { showShiny = it }
                )
            }

            // Image depending on shiny toggle
            val imageUrl = if (showShiny)
                details.sprites.other?.officialArtwork?.frontShiny
            else
                details.sprites.other?.officialArtwork?.frontDefault

            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = details.name,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "#${details.id} ${details.name.replaceFirstChar { it.uppercase() }}")
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Types: ${details.types.joinToString { it.type.name.replaceFirstChar { it.uppercase() } }}"
            )

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
    navController: NavHostController,
    isOnline: Boolean
) {
    val pokemonList = viewModel.pokemonList.observeAsState(initial = emptyList())
    val cachedIds = viewModel.cachedPokemonSet.observeAsState(initial = emptySet())

    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp)) {
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
        var filteredList = pokemonList.value.filter {
            it.name.lowercase().contains(query) || it.id?.toString() == query
        }

        //filter to only cached entries if not online
        if (!isOnline) {
            filteredList = pokemonList.value.filter { pokemon ->
                cachedIds.value.contains(pokemon.id)
            }
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

    val containerColor = MaterialTheme.colorScheme.surfaceContainer

    var isFavorite by remember { mutableStateOf(viewModel.isPokemonFavourite(pokemon.id!!)) }

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
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = pokemon.name,
                    modifier = Modifier
                        .size(56.dp)
                        .padding(end = 16.dp)
                )
                Column {
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

            IconButton(
                onClick = {
                    val id = pokemon.id ?: return@IconButton
                    viewModel.setPokemonFavourite(id, !isFavorite)
                    isFavorite = viewModel.isPokemonFavourite(pokemon.id!!)
                }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (isFavorite) "Unstar" else "Star",
                    tint = if (isFavorite) Color.Yellow else Color.Gray
                )
            }
        }
    }
}

