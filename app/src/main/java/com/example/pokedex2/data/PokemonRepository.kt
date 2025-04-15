package com.example.pokedex2.data

import android.util.Log
import com.example.pokedex2.network.PokeApi
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

data class CacheStats(
    var jsonCount: Int = 0,
    var jsonSize: Double = 0.0,
    var imgCount: Int = 0,
    var imgSize: Double = 0.0,
    var totalEntries: Int = 0
) {
    val totalSize: Double get() = jsonSize + imgSize
    override fun toString(): String {
        return String.format(locale = null,"Json Count: %d\nJson Size: %.2f MB\nImage Count: %d\nImage Size: %.2f MB", jsonCount, jsonSize / 1000 / 1000, imgCount, imgSize / 1000 / 1000)
    }
}

class PokemonRepository(){
    companion object {
        //used to store cacheDir because context is too much
        lateinit var cacheDir: File
        lateinit var filesDir: File
    }

    fun isPokemonFavourite(id: Int): Boolean {
        val file = File(filesDir, "favourites.txt")
        if (!file.exists()) {
            Log.d("CACHE", "favourites.txt not found")
            return false
        }
        Log.d("CACHE", "favourites.txt found! size: ${file.length()}")
        val lines = file.readLines()
        for (line in lines) {
            if (line.toInt() == id) {
                return true
            }
        }
        return false
    }

    fun setPokemonFavourite(id: Int, status: Boolean) {
        val file = File(filesDir, "favourites.txt")
        if (!file.exists()) {
            Log.d("CACHE", "favourites.txt not found")
            file.createNewFile()
        }
        Log.d("CACHE", "favourites.txt found! size: ${file.length()}")
        val lines = file.readLines().toMutableList()
        if (status) {
            lines.add(id.toString())
        } else {
            lines.remove(id.toString())
        }
        file.writeText(lines.joinToString("\n"))
    }

    fun generatePokemonDetailsMap(): HashMap<Int, File> {
        val res: HashMap<Int, File> = HashMap<Int, File>()
        val idPattern = """pokedetails_([0-9]+)""".toRegex()
        for (file in cacheDir.listFiles()) {
            if (!file.isFile) continue
            val match = idPattern.find(file.name)
            val id = match?.groupValues?.get(1)?.toInt() ?: continue
            res[id] = file
        }
        return res
    }

    fun getCacheStats(): CacheStats {
        val stats = CacheStats()
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
        return stats
    }


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

    fun clearCache() {
        try {
            for (file in cacheDir.listFiles()) {
                if (!file.isFile) continue
                file.delete()
            }
            val imgCache = File(cacheDir, "image_cache")
            for (file in imgCache.listFiles()) {
                if (!file.isFile) continue
                file.delete()
            }
            Log.d("CACHE", "Cache cleared")
        } catch (e: Exception) {
            Log.d("CACHE", "Error clearing cache: ${e.toString()}")
        }
    }

    fun clearImageCache() {
        try {
            val imgCache = File(cacheDir, "image_cache")
            for (file in imgCache.listFiles()) {
                if (!file.isFile) continue
                file.delete()
            }
            Log.d("CACHE", "Image cache cleared")
        } catch (e: Exception) {
            Log.d("CACHE", "Error clearing image cache: ${e.toString()}")
        }
    }

    fun clearPokemonCache() {
        try {
            for (file in cacheDir.listFiles()) {
                //there will be one dir (image_cache) to ignore
                if (!file.isFile) continue
                file.delete()
            }
            Log.d("CACHE", "Pokemon cache cleared")
        } catch (e: Exception) {
            Log.d("CACHE", "Error clearing pokemon cache: ${e.toString()}")
        }
    }


}
