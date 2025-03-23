package com.example.pokedex2.network

//this is the data class that is returned when getting a single pokemon
data class PokemonDetails(
    val id: Int,
    val name: String,
    val weight: Int,
    val height: Int
)
