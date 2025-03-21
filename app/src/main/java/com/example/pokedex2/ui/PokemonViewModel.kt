package com.example.pokedex2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex2.network.PokeApi
import com.example.pokedex2.network.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonViewModel : ViewModel() {
    private val _pokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemonList: LiveData<List<Pokemon>> get() = _pokemonList

    init {
        getPokemon(20, 0)
    }

    fun getPokemon(limit: Int, offset: Int) {
        viewModelScope.launch {
            try {
                val response = PokeApi.retrofitService.getPokemon(limit, offset)
                _pokemonList.value = response.results
            } catch (e: Exception) {

            }
        }
    }
}