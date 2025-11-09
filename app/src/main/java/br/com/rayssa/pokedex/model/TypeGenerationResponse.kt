package br.com.rayssa.pokedex.model

import com.google.gson.annotations.SerializedName

// Resposta mínima para /type/{type}
data class TypeResponse(
    val id: Int,
    val name: String,
    val pokemon: List<PokemonContainer>
)

data class PokemonContainer(
    val pokemon: PokemonResult
)

// Resposta mínima para /generation/{id}
data class GenerationResponse(
    val id: Int,
    val name: String,
    @SerializedName("pokemon_species")
    val pokemonSpecies: List<PokemonResult>
)
