package br.com.rayssa.pokedex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.rayssa.pokedex.databinding.ActivityMainBinding
import br.com.rayssa.pokedex.viewmodel.PokemonListViewModel
import br.com.rayssa.pokedex.model.Pokemon

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PokemonListViewModel
    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[PokemonListViewModel::class.java]
        binding.viewModel = viewModel

        // Ajustar paddings para insets do sistema (status bar / navigation bar)
        val originalPaddingLeft = binding.root.paddingLeft
        val originalPaddingTop = binding.root.paddingTop
        val originalPaddingRight = binding.root.paddingRight
        val originalPaddingBottom = binding.root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sysInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                originalPaddingLeft + sysInsets.left,
                originalPaddingTop + sysInsets.top,
                originalPaddingRight + sysInsets.right,
                originalPaddingBottom + sysInsets.bottom
            )
            insets
        }

        val adapter = PokemonAdapter { pokemon ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("pokemon", pokemon)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.pokemonListLiveData.observe(this) { pokemonList ->
            adapter.submitList(pokemonList)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.lottieLoading.visibility = View.VISIBLE
                binding.lottieLoading.playAnimation()
            } else {
                binding.lottieLoading.pauseAnimation()
                binding.lottieLoading.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        Log.d(tag, "onCreate chamado")

        binding.searchEditText.requestFocus()

        val typesArray = resources.getStringArray(R.array.pokemon_types)
        val gensArray = resources.getStringArray(R.array.pokemon_generations)

        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, typesArray)
        val genAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, gensArray)

        val typeDropdown = binding.root.findViewById<android.widget.AutoCompleteTextView>(R.id.type_dropdown)
        val genDropdown = binding.root.findViewById<android.widget.AutoCompleteTextView>(R.id.gen_dropdown)

        typeDropdown.setAdapter(typeAdapter)
        genDropdown.setAdapter(genAdapter)

    // Inicializar vazio para que o hint do TextInputLayout permaneça visível e os labels não façam parte da lista selecionável
    typeDropdown.setText("", false)
    genDropdown.setText("", false)

        // Ao clicar/focar, abrir a lista
        typeDropdown.setOnClickListener { typeDropdown.showDropDown() }
        typeDropdown.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) typeDropdown.showDropDown() }

        genDropdown.setOnClickListener { genDropdown.showDropDown() }
        genDropdown.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) genDropdown.showDropDown() }

        // Função que lê seleções/consulta e aplica filtros no ViewModel
        val applyCurrentFilters: () -> Unit = {
            val selectedTypePos = typesArray.indexOf(typeDropdown.text.toString())
            val selectedType = if (selectedTypePos <= 0) null else typesArray[selectedTypePos].lowercase()

            val selectedGenPos = gensArray.indexOf(genDropdown.text.toString())
            val generationId = if (selectedGenPos <= 0) null else selectedGenPos

            val query = binding.searchEditText.text.toString().trim()
            viewModel.applyFilters(selectedType, generationId, query)
        }

        typeDropdown.setOnItemClickListener { parent, view, position, id ->
            applyCurrentFilters()
            binding.recyclerView.scrollToPosition(0) // Ao aplicar um filtro via dropdown, rolar lista para o topo
        }

        genDropdown.setOnItemClickListener { parent, view, position, id ->
            applyCurrentFilters()
            binding.recyclerView.scrollToPosition(0) // Ao aplicar um filtro via dropdown, rolar lista para o topo
        }

        binding.searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                applyCurrentFilters()
            }
        })
    }

    override fun onStart() { super.onStart(); Log.d(tag, "onStart chamado") }
    override fun onResume() { super.onResume(); Log.d(tag, "onResume chamado") }
    override fun onPause() { super.onPause(); Log.d(tag, "onPause chamado") }
    override fun onStop() { super.onStop(); Log.d(tag, "onStop chamado") }
    override fun onDestroy() { super.onDestroy(); Log.d(tag, "onDestroy chamado") }
    override fun onRestart() { super.onRestart(); Log.d(tag, "onRestart chamado") }
}