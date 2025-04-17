package com.example.pokedex2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex2.data.CacheStats
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

    private val _cacheStats = MutableLiveData<CacheStats>()
    val cacheStats: LiveData<CacheStats> get() = _cacheStats

    private val _favorites = MutableLiveData<Set<Int>>(setOf())
    val favorites: LiveData<Set<Int>> get() = _favorites



    private val repo = PokemonRepository()

    init {
        getPokemon(10000, 0)
//        initDetailsMap()
    }

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

    fun getCacheStats() {
        viewModelScope.launch {
            _cacheStats.value = repo.getCacheStats()
        }
    }

    enum class ClearOption {
        ALL,
        IMAGES,
        POKEMON
    }

    fun clearCache(option: ClearOption) {
        viewModelScope.launch {
            when (option) {
                ClearOption.ALL -> repo.clearCache()
                ClearOption.IMAGES -> repo.clearImageCache()
                ClearOption.POKEMON -> repo.clearPokemonCache()
            }
            getCacheStats()
        }
    }

    fun isPokemonFavourite(id: Int): Boolean {
        return _favorites.value?.contains(id) ?: false
    }

    fun setPokemonFavourite(id: Int, status: Boolean) {
        val currentFavs = _favorites.value ?: mutableSetOf()
        val updatedFavs = currentFavs.toMutableSet() // Make a copy to trigger LiveData observers
        if (status) {
            updatedFavs.add(id)
        } else {
            updatedFavs.remove(id)
        }
        _favorites.value = updatedFavs
    }
}