package com.example.pokedex2.data

import android.util.Log
import com.example.pokedex2.network.PokeApi
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

data class CacheStats(
    var jsonCount: Int = 0,
    var jsonSize: Long = 0,
    var imgCount: Int = 0,
    var imgSize: Long = 0,
    var totalEntries: Int = 0
) {
    val totalSize: Long get() = jsonSize + imgSize
    override fun toString(): String {
        return "Json Count: ${jsonCount}\nJson Size: ${jsonSize}\nImage Count: ${imgCount}\nImage Size: ${imgSize}"
    }
}

class PokemonRepository(){
    companion object {
        lateinit var cacheDir: File
        var stats: CacheStats = CacheStats()

        //on startup, total file counts will be refreshed
        fun updateCacheStats() {
            try {
                val imgCache = File(cacheDir, "image_cache")
                for (file in cacheDir.listFiles()) {
                    if (!file.isFile) continue
                    stats.jsonCount++
                    stats.jsonSize += file.length()
                }
                for (file in imgCache.listFiles()) {
                    if (!file.isFile) continue
                    stats.imgCount++
                    stats.imgSize += file.length()
                }
                Log.d("CACHE STATS", stats.toString())
            } catch (e: Exception) {
                Log.d("CACHE STATS", "Error updating cache stats: ${e.toString()}")
            }

        }
    }

//    fun generatePokemonDetailsMap(): HashMap<Int, File> {
//        val res: HashMap<Int, File> = HashMap<Int, File>()
//        val idPattern = """pokedetails_([0-9]+)""".toRegex()
//        for (file in cacheDir.listFiles()) {
//            if (!file.isFile) continue
//            val match = idPattern.find(file.name)
//            val id = match?.groupValues?.get(1)?.toInt() ?: continue
//            res[id] = file
//        }
//        return res
//    }

    fun savePokelistToCache(pokeList: List<Pokemon>) {
        val file = File(cacheDir, "pokelist.json")
        val gson = Gson()
        val json = gson.toJson(pokeList)
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }
        Log.d("CACHE", "Wrote pokelist.json with size ${file.length()}")
    }

    fun getPokelistFromCache(): List<Pokemon> {
        val file = File(cacheDir, "pokelist.json")
        if (!file.exists()) {
            Log.d("CACHE", "pokelist.json not found")
            return emptyList<Pokemon>()
        }
        Log.d("CACHE", "pokelist.json found! size: ${file.length()}")
        val gson = Gson()
        val json = FileInputStream(file).bufferedReader().use { it.readText() }
        return gson.fromJson(json, Array<Pokemon>::class.java).toList()
    }

    fun savePokedetailsToCache(
        pokemon: PokemonDetails,
//        pokemonDetailsMap: LiveData<HashMap<Int, File>>
    ) {
        val id = pokemon.id;
        val file = File(cacheDir, "pokedetails_${id}.json")
        val gson = Gson()
        val json = gson.toJson(pokemon)
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }
//        pokemonDetailsMap.value?.set(id, file)
//        Log.d("MAP", "Map value added, size now ${pokemonDetailsMap.value?.size}")
        Log.d("CACHE", "Wrote ${pokemon.name} with size ${file.length()}")
    }

    fun getPokedetailsFromCache(id: Int): PokemonDetails? {
        val file = File(cacheDir, "pokedetails_${id}.json")
        if (!file.exists()) {
            Log.d("CACHE", "Cache file for ${id} not found")
            return null
        }
        Log.d("CACHE", "pokemondetails_${id}.json found! size: ${file.length()}")

        val gson = Gson()
        val json = FileInputStream(file).bufferedReader().use { it.readText() }
        return gson.fromJson(json, PokemonDetails::class.java)
    }


    suspend fun getPokemon(limit: Int, offset: Int): List<Pokemon> {
        try {
            var cachedPokemon = getPokelistFromCache()
//            cache = emptyList<Pokemon>()
            if (cachedPokemon == emptyList<Pokemon>()) {
                val response = PokeApi.retrofitService.getPokemon(limit, offset)
                val idPattern = """([0-9]*)/$""".toRegex()
                val pokeList = response.results.mapIndexed { index, pokemon ->
                    val match = idPattern.find(pokemon.url)
                    val id = match?.groupValues?.get(1)?.toInt()
                    pokemon.copy(id = id ?: index)
                }
                stats.totalEntries = pokeList.size
                savePokelistToCache(pokeList)
                return pokeList
            } else {
                return cachedPokemon
            }

        } catch (e: Exception) {
            Log.d("ERR", e.toString())
        }
        return emptyList()
    }

    suspend fun getPokemonById(id: Int): PokemonDetails? {
        try {
            var cachedPokemon = getPokedetailsFromCache(id)
            if (cachedPokemon != null) {
                return cachedPokemon
            } else {
                val response = PokeApi.retrofitService.getPokemonById(id.toString())
                savePokedetailsToCache(response)
                return response
            }

        } catch (e: Exception) {
            Log.d("ERR", e.toString())
        }
        return null
    }


}
