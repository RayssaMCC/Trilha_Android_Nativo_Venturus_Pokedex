package br.com.rayssa.pokedex.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import br.com.rayssa.pokedex.databinding.FragmentPokemonDetailBinding
import br.com.rayssa.pokedex.model.Pokemon
import br.com.rayssa.pokedex.viewmodel.DetailViewModel

class PokemonDetailFragment : Fragment() {

    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera o Pokémon passado pela Activity (via argumento)
        val pokemon = if (android.os.Build.VERSION.SDK_INT >= 33) {
            arguments?.getParcelable("pokemon", Pokemon::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<Pokemon>("pokemon")
        }

        // Observa estado de carregamento
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.loadingOverlay.visibility = View.VISIBLE
                binding.lottiePokeball.playAnimation()
            } else {
                binding.lottiePokeball.pauseAnimation()
                binding.loadingOverlay.visibility = View.GONE
            }
        })

        // Observa os dados do Pokémon
        viewModel.pokemonDetail.observe(viewLifecycleOwner, Observer { pokemonDetail ->
            pokemonDetail?.let {
                binding.pokemonName.text = it.name
                binding.pokemonId.text = it.id.toString()
                binding.pokemonImage.contentDescription = it.name
            }
        })

        // Mostra loading inicial
        viewModel.showLoading()
        view.postDelayed({
            viewModel.hideLoading()
        }, 1800) // simula 1.8 segundos de carregamento
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
