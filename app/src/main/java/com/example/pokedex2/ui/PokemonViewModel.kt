package com.example.pokedex2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex2.data.Pokemon
import com.example.pokedex2.data.PokemonDetails
import com.example.pokedex2.data.PokemonUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.pokedex2.data.PokemonRepository

class PokemonViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PokemonUiState())
    val uiState: StateFlow<PokemonUiState> = _uiState.asStateFlow()

    private val _pokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemonList: LiveData<List<Pokemon>> get() = _pokemonList

    private val _currentPokemonDetails = MutableLiveData<PokemonDetails?>(null)
    val currentPokemonDetails: MutableLiveData<PokemonDetails?> get() = _currentPokemonDetails

//    private val _detailedPokemonList = MutableLiveData<List<PokemonDetails>>(emptyList())
//    val detailedPokemonList: LiveData<List<PokemonDetails>> get() = _detailedPokemonList
    private val repo = PokemonRepository()

    init {
        getPokemon(10000, 0)
    }

    fun getPokemon(limit: Int, offset: Int) {
        viewModelScope.launch {
            _pokemonList.value = repo.getPokemon(limit, offset)
        }
    }

    fun getPokemonById(id: Int) {
        viewModelScope.launch {
            _currentPokemonDetails.value = repo.getPokemonById(id)
        }
    }
}