package br.com.rayssa.pokedex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rayssa.pokedex.model.PokemonDetailResponse
import br.com.rayssa.pokedex.network.RetrofitClient
import kotlinx.coroutines.launch
import br.com.rayssa.pokedex.model.PokemonSpeciesResponse

class DetailViewModel : ViewModel() {

    private val _pokemonDetail = MutableLiveData<PokemonDetailResponse?>()
    val pokemonDetail: LiveData<PokemonDetailResponse?> = _pokemonDetail

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Chama o fragment
    fun showLoading() {
        _isLoading.value = true
    }

    fun hideLoading() {
        _isLoading.value = false
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _flavorText = MutableLiveData<String?>(null)
    val flavorText: LiveData<String?> = _flavorText

    fun fetchPokemonDetails(pokemonId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = RetrofitClient.pokeApiService.getPokemonById(pokemonId)
                _pokemonDetail.value = response
                // Buscar dados da espécie para obter os textos descritivos (flavor_text_entries)
                try {
                    val speciesResponse: PokemonSpeciesResponse = RetrofitClient.pokeApiService.getPokemonSpecies(pokemonId.toString())

                    // Lógica de seleção: priorizar entradas em inglês; entre elas, preferir a versão "red"
                    val enRed = speciesResponse.flavorTextEntries.firstOrNull {
                        it.language.name.equals("en", true) && it.version.name.equals("red", true)
                    }

                    val enAny = speciesResponse.flavorTextEntries.firstOrNull {
                        it.language.name.equals("en", true)
                    }

                    val chosen = enRed?.flavorText ?: enAny?.flavorText ?: speciesResponse.flavorTextEntries.firstOrNull()?.flavorText

                    // Limpar espaços em branco e quebras de linha retornadas pela API
                    _flavorText.value = chosen?.replace(Regex("\\s+"), " ")?.trim()
                    } catch (e: Exception) {
                    // Não-fatal: chamada de species pode falhar; definir nulo e continuar
                    _flavorText.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar detalhes: ${e.message}"
                _pokemonDetail.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}