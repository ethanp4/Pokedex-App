package com.example.pokedex2.network

import com.example.pokedex2.data.Pokemon
import com.example.pokedex2.data.PokemonDetails
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://pokeapi.co/api/v2/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemon(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonById(
        @Path("id") id: String
    ): PokemonDetails
}

data class PokemonResponse(
    val results: List<Pokemon>
)

object PokeApi {
    val retrofitService: PokeApiService by lazy {
        retrofit.create(PokeApiService::class.java)
    }
}