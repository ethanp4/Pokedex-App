package com.example.pokedex2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex2.network.PokeApi
import com.example.pokedex2.network.Pokemon
import com.example.pokedex2.network.PokemonDetails
import com.example.pokedex2.network.PokemonUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PokemonUiState())
    val uiState: StateFlow<PokemonUiState> = _uiState.asStateFlow()

    private val _pokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemonList: LiveData<List<Pokemon>> get() = _pokemonList

    private val _currentPokemonDetails = MutableLiveData<PokemonDetails>(null)
    val currentPokemonDetails: LiveData<PokemonDetails> get() = _currentPokemonDetails

//    private val _detailedPokemonList = MutableLiveData<List<PokemonDetails>>(emptyList())
//    val detailedPokemonList: LiveData<List<PokemonDetails>> get() = _detailedPokemonList

    init {
        getPokemon(20, 0)
    }

    fun getPokemon(limit: Int, offset: Int) {
        viewModelScope.launch {
            try {
                val response = PokeApi.retrofitService.getPokemon(limit, offset)
                val pokemonListIds = response.results.mapIndexed { index, pokemon ->
                    pokemon.copy(id = index)
                }
                _pokemonList.value = pokemonListIds
            } catch (e: Exception) {

            }
        }
    }

    fun getPokemonById(id: Int) {
        viewModelScope.launch {
            try {
                val response = PokeApi.retrofitService.getPokemonById(id.toString())
                _currentPokemonDetails.value = response
            } catch (e: Exception) {

            }
        }
    }
}