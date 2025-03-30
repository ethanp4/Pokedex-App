package com.example.pokedex2.data

import android.util.Log
import com.example.pokedex2.network.PokeApi
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PokemonRepository(){
    companion object {
        lateinit var cacheDir: File
    }

    fun savePokelistToCache(pokeList: List<Pokemon>) {
        val file = File(cacheDir, "pokelist_cache.json")
        val gson = Gson()
        val json = gson.toJson(pokeList)
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }
        Log.d("CACHE", "Written pokelist_cache.json")
    }

    fun getPokelistFromCache(): List<Pokemon> {
        val file = File(cacheDir, "pokelist_cache.json")
        if (!file.exists()) return emptyList<Pokemon>()

        val gson = Gson()
        val json = FileInputStream(file).bufferedReader().use { it.readText() }
        return gson.fromJson(json, Array<Pokemon>::class.java).toList()
    }

    suspend fun getPokemon(limit: Int, offset: Int): List<Pokemon> {
        try {
            var cache = getPokelistFromCache()
            cache = emptyList<Pokemon>()
            if (cache == emptyList<Pokemon>()) {
                val response = PokeApi.retrofitService.getPokemon(limit, offset)
                val idPattern = """([0-9]*)/$""".toRegex()
                val pokeList = response.results.mapIndexed { index, pokemon ->
                    val match = idPattern.find(pokemon.url)
                    val id = match?.groupValues?.get(1)?.toInt()
                    pokemon.copy(id = id ?: index)
                }
                savePokelistToCache(pokeList)
                return pokeList
            } else {
                Log.d("CACHE", "Displaying cached list")
                return cache
            }

        } catch (e: Exception) {
            Log.d("ERR", e.toString())
        }
        return emptyList()
    }

    suspend fun getPokemonById(id: Int): PokemonDetails? {
        try {
            val response = PokeApi.retrofitService.getPokemonById(id.toString())
            return response
        } catch (e: Exception) {
            Log.d("ERR", e.toString())
        }
        return null
    }

}
