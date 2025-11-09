package br.com.rayssa.pokedex.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import br.com.rayssa.pokedex.R
import android.widget.TextView
import br.com.rayssa.pokedex.model.PokemonDetailResponse
import kotlin.math.*

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        view.setImageResource(R.drawable.ic_pokemon_placeholder)
        return
    }
    view.load(url) {
        crossfade(true)
        placeholder(R.drawable.ic_pokemon_placeholder)
        error(R.drawable.ic_error)
    }
}

@BindingAdapter("pokemonTypes")
fun setPokemonTypes(view: TextView, pokemonDetail: PokemonDetailResponse?) {
    pokemonDetail?.let {
        val types = it.types.joinToString(", ") { type ->
            type.type.name.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }
        }
        view.text = types
    }
}

@BindingAdapter("pokemonHeight")
fun setPokemonHeight(view: TextView, height: Int?) {
    height?.let {
        val heightInMeters = it / 10.0
        view.text = String.format("%.1f m", heightInMeters)
    }
}

@BindingAdapter("pokemonWeight")
fun setPokemonWeight(view: TextView, weight: Int?) {
    weight?.let {
        val weightInKg = it / 10.0
        view.text = String.format("%.1f kg", weightInKg)
    }
}

@BindingAdapter("pokemonStat")
fun setPokemonStat(view: TextView, pokemonDetail: PokemonDetailResponse?) {
    val statName = view.tag as? String ?: return
    pokemonDetail?.let {
        val stat = it.stats.find { statItem ->
            statItem.stat.name == statName
        }
        view.text = stat?.baseStat?.toString() ?: "0"
    }
}

@BindingAdapter("pokemonGeneration")
fun setPokemonGeneration(view: TextView, pokemonDetail: PokemonDetailResponse?) {
    pokemonDetail?.let {
        val id = it.id
        val genText = when (id) {
            in 1..151 -> "Gen 1 (Kanto)"
            in 152..251 -> "Gen 2 (Johto)"
            in 252..386 -> "Gen 3 (Hoenn)"
            in 387..493 -> "Gen 4 (Sinnoh)"
            in 494..649 -> "Gen 5 (Unova)"
            in 650..721 -> "Gen 6 (Kalos)"
            in 722..809 -> "Gen 7 (Alola)"
            in 810..898 -> "Gen 8 (Galar)"
            else -> "Gen 9 (Paldea)"
        }
        view.text = genText
    }
}