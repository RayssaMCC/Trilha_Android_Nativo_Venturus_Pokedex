package br.com.rayssa.pokedex.network

import br.com.rayssa.pokedex.model.PokemonResponse
import br.com.rayssa.pokedex.model.PokemonDetailResponse
import br.com.rayssa.pokedex.model.TypeResponse
import br.com.rayssa.pokedex.model.GenerationResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(@Query("limit") limit: Int = 20, @Query("offset") offset: Int = 0): PokemonResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonByName(@Path("name") name: String): PokemonDetailResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") id: Int): PokemonDetailResponse

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(@Path("name") name: String): br.com.rayssa.pokedex.model.PokemonSpeciesResponse

    @GET("type/{type}")
    suspend fun getPokemonByType(@Path("type") type: String): TypeResponse

    @GET("generation/{id}")
    suspend fun getPokemonByGeneration(@Path("id") id: Int): GenerationResponse
}
