package com.worldcup.albumtracker.ui.album

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.databinding.FragmentAlbumBinding
import com.worldcup.albumtracker.ui.register.RegisterActivity
import com.worldcup.albumtracker.utils.viewModelFactory

/**
 * Pantalla del Álbum completo.
 *
 * Combina los requisitos académicos de consultar láminas obtenidas,
 * pendientes y repetidas mediante pestañas, más un filtro por selección
 * nacional. Tocar una lámina abre el flujo de registro de esa lámina.
 */
class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AlbumViewModel by viewModels { viewModelFactory() }

    private val adapter = StickerAdapter { sticker ->
        // Al tocar una lámina se abre el registro con el número precargado.
        startActivity(
            Intent(requireContext(), RegisterActivity::class.java)
                .putExtra(RegisterActivity.EXTRA_STICKER_NUMBER, sticker.number)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerStickers.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerStickers.adapter = adapter

        setupTabs()
        observeViewModel()

        viewModel.loadCountries()
        viewModel.selectTab(AlbumTab.ALL)
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val selected = when (tab.position) {
                    0 -> AlbumTab.ALL
                    1 -> AlbumTab.OBTAINED
                    2 -> AlbumTab.MISSING
                    else -> AlbumTab.REPEATED
                }
                viewModel.selectTab(selected)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
    }

    private fun setupCountryFilter(countries: List<String>) {
        val options = mutableListOf(getString(R.string.filter_all))
        options.addAll(countries)

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            options
        )
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        binding.spinnerCountry.adapter = spinnerAdapter

        binding.spinnerCountry.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val country = if (position == 0) null else options[position]
                    viewModel.selectCountry(country)
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
            }
    }

    private fun observeViewModel() {
        viewModel.countries.observe(viewLifecycleOwner) { setupCountryFilter(it) }
        viewModel.stickers.observe(viewLifecycleOwner) { stickers ->
            adapter.submitList(stickers)
            val isEmpty = stickers.isEmpty()
            binding.emptyState.root.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.recyclerStickers.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresca para reflejar registros hechos desde otras pantallas.
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
