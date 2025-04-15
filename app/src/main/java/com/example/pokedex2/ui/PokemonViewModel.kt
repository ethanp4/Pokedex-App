package com.example.pokedex2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex2.data.Pokemon
import com.example.pokedex2.data.PokemonDetails
import com.example.pokedex2.data.PokemonRepository
import com.example.pokedex2.data.PokemonUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PokemonUiState())
    val uiState: StateFlow<PokemonUiState> = _uiState.asStateFlow()

    private val _pokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemonList: LiveData<List<Pokemon>> get() = _pokemonList

    private val _currentPokemonDetails = MutableLiveData<PokemonDetails?>(null)
    val currentPokemonDetails: LiveData<PokemonDetails?> get() = _currentPokemonDetails

    private val repo = PokemonRepository()

//    private val _pokemonDetailsMap = MutableLiveData<HashMap<Int, File>>(hashMapOf())
//    val pokemonDetailsMap: LiveData<HashMap<Int, File>> get() = _pokemonDetailsMap

    init {
        getPokemon(10000, 0)
//        initDetailsMap()
    }

//    private fun initDetailsMap() {
//        viewModelScope.launch {
//            //contains id -> cache file for available cached pokemon
////            _pokemonDetailsMap.value = repo.generatePokemonDetailsMap()
//            Log.d("MAP", "Map has ${_pokemonDetailsMap.value!!.count()} items")
//        }
//    }

    fun getPokemon(limit: Int, offset: Int) {
        if (_pokemonList.value?.isEmpty() == false) return
        viewModelScope.launch {
            _pokemonList.value = repo.getPokemon(limit, offset)
        }
    }

    fun getPokemonById(id: Int) {
        if (_currentPokemonDetails.value?.id == id) return
        viewModelScope.launch {
            _currentPokemonDetails.value = repo.getPokemonById(id)
        }
    }

    fun isPokemonFavourite(id: Int): Boolean {
        return false
    }

    fun setPokemonFavourite(id: Int, status: Boolean) {

    }
}