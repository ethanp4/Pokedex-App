package com.example.pokedex2.data

//this is the data class that is returned when fetching a list of pokemon
//it doesnt return any details
data class Pokemon(
    val id: Int?,
    val name: String,
    val url: String,
    val type: String? = null
)
