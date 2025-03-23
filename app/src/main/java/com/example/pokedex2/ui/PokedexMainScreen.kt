package com.example.pokedex2.ui

import com.example.pokedex2.PokemonViewModel
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.pokedex2.network.Pokemon

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
            composable(route = PokedexMainScreen.Details.name) {
                PokemonDetails(
                    selectedPokemon = uiState.selectedPokemonId,
                    viewModel = viewModel
                )
            }
        }
    }
}

//this composable is the details screen
@Composable
fun PokemonDetails(selectedPokemon: Int, viewModel: PokemonViewModel) {
    //send a get request
    viewModel.getPokemonById(selectedPokemon)
    //observe the variable in viewmodel for the result
    val pokemon = viewModel.currentPokemonDetails.observeAsState()
    if (pokemon.value == null) {
        Text("Loading")
    } else {
        Column {
            Text("ID is ${pokemon.value?.id}")
            Text("Weight is ${pokemon.value?.weight}")
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
    //observe the pokemonList for updates
    //getPokemon is automatically called on init
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
    Card (modifier = Modifier.fillMaxWidth(), onClick = {
        viewModel.uiState.value.selectedPokemonId = pokemon.id
        navController.navigate(PokedexMainScreen.Details.name)
        Log.d("Click", "${pokemon.name} was clicked")
        Log.d("Click", "Selected pokemon id: ${viewModel.uiState.value.selectedPokemonId}")
    }) {
        Text(text = "ID: " + pokemon.id)
        Text(text = pokemon.name)
    }
}
