package br.com.rayssa.pokedex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rayssa.pokedex.model.Pokemon
import br.com.rayssa.pokedex.model.PokemonResult
import br.com.rayssa.pokedex.model.TypeResponse
import br.com.rayssa.pokedex.model.GenerationResponse
import br.com.rayssa.pokedex.network.RetrofitClient
import kotlinx.coroutines.launch

class PokemonListViewModel : ViewModel() {

    private val _pokemonListLiveData = MutableLiveData<List<Pokemon>>()
    val pokemonListLiveData: LiveData<List<Pokemon>> = _pokemonListLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadPokemonFromApi()
    }

    // filtros atualmente aplicados
    private var currentTypeFilter: String? = null
    private var currentGenerationFilter: Int? = null

    private fun loadPokemonFromApi() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.pokeApiService.getPokemonList(1328)
                val pokemonList = response.results.map { result ->
                    val id = result.url.split("/").dropLast(1).last().toInt()
                    val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
                    Pokemon(id, result.name.replaceFirstChar { it.uppercase() }, imageUrl)
                }
                _pokemonListLiveData.value = pokemonList
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Sem internet ou erro na sincronização: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Aplica filtros de tipo/geração/nome. Se type == null ou "All" ignora; geração null ou 0 ignora.
    fun applyFilters(type: String?, generationId: Int?, query: String?) {
        currentTypeFilter = type?.takeIf { it.isNotBlank() && it.lowercase() != "all" }?.lowercase()
        currentGenerationFilter = generationId?.takeIf { it > 0 }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val lists = mutableListOf<List<Pokemon>>()

                // se houver filtro por tipo
                currentTypeFilter?.let { t ->
                    val typeResponse = RetrofitClient.pokeApiService.getPokemonByType(t)
                    val listByType = typeResponse.pokemon.map { container ->
                        val name = container.pokemon.name
                        val id = container.pokemon.url.split("/").dropLast(1).last().toInt()
                        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
                        Pokemon(id, name.replaceFirstChar { it.uppercase() }, imageUrl)
                    }
                    lists.add(listByType)
                }

                // se houver filtro por geração
                currentGenerationFilter?.let { genId ->
                    val genResponse = RetrofitClient.pokeApiService.getPokemonByGeneration(genId)
                    val listByGen = genResponse.pokemonSpecies.map { species ->
                        val name = species.name
                        val id = species.url.split("/").dropLast(1).last().toInt()
                        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
                        Pokemon(id, name.replaceFirstChar { it.uppercase() }, imageUrl)
                    }
                    lists.add(listByGen)
                }

                val resultList: List<Pokemon> = when (lists.size) {
                    0 -> { // sem filtros de tipo/geração -> usa lista completa carregada
                        _pokemonListLiveData.value ?: emptyList()
                    }
                    1 -> lists[0]
                    else -> { // interseção entre os resultados
                        val setIds = lists[0].map { it.id }.toSet()
                        lists[1].filter { it.id in setIds }
                    }
                }

                // aplicar filtro de nome (search)
                val finalList = if (!query.isNullOrBlank()) {
                    resultList.filter { it.name.contains(query, ignoreCase = true) }
                } else resultList

                // Garantir ordem consistente: ordenar por ID crescente antes de exibir
                val sortedFinal = finalList.sortedBy { it.id }

                _pokemonListLiveData.value = sortedFinal
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao aplicar filtros: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}