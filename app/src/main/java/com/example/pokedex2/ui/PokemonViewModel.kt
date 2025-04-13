package com.example.pokedex2

import android.util.Log
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
import java.io.File

class PokemonViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PokemonUiState())
    val uiState: StateFlow<PokemonUiState> = _uiState.asStateFlow()

    private val _pokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemonList: LiveData<List<Pokemon>> get() = _pokemonList

    private val _currentPokemonDetails = MutableLiveData<PokemonDetails?>(null)
    val currentPokemonDetails: LiveData<PokemonDetails?> get() = _currentPokemonDetails

    private val repo = PokemonRepository()

    private val _pokemonDetailsMap = MutableLiveData<HashMap<Int, File>>(hashMapOf())
    val pokemonDetailsMap: LiveData<HashMap<Int, File>> get() = _pokemonDetailsMap

    init {
        getPokemon(10000, 0)
        initDetailsMap()
    }

    private fun initDetailsMap() {
        viewModelScope.launch {
            //contains id -> cache file for available cached pokemon
            _pokemonDetailsMap.value = repo.generatePokemonDetailsMap()
            Log.d("MAP", "Map has ${_pokemonDetailsMap.value!!.count()} items")
        }
    }

    fun getPokemon(limit: Int, offset: Int) {
        viewModelScope.launch {
            _pokemonList.value = repo.getPokemon(limit, offset)
        }
    }

    fun getPokemonById(id: Int) {
        viewModelScope.launch {
            _currentPokemonDetails.value = repo.getPokemonById(id, pokemonDetailsMap)
        }
    }

    fun isPokemonFavourite(id: Int): Boolean {
        return false
    }

    fun setPokemonFavourite(id: Int, status: Boolean) {

    }
}