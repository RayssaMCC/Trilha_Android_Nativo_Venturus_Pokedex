package br.com.rayssa.pokedex.model

import com.google.gson.annotations.SerializedName

data class PokemonSpeciesResponse(
    @SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>
)

data class FlavorTextEntry(
    @SerializedName("flavor_text")
    val flavorText: String,
    val language: NamedAPIResource,
    val version: NamedAPIResource
)

data class NamedAPIResource(
    val name: String
)
