package com.worldcup.albumtracker.ui.statistics

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.data.model.AlbumStats
import com.worldcup.albumtracker.databinding.FragmentStatisticsBinding
import com.worldcup.albumtracker.utils.viewModelFactory

/**
 * Pantalla de Estadísticas: muestra el resumen del álbum (obtenidas,
 * faltantes, repetidas, porcentaje) y el ranking de selecciones más
 * completadas con barras de progreso.
 */
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels { viewModelFactory() }

    private val countryAdapter = CountryProgressAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerCountries.adapter = countryAdapter

        viewModel.stats.observe(viewLifecycleOwner) { renderStats(it) }
        viewModel.countries.observe(viewLifecycleOwner) { countryAdapter.submitList(it) }
    }

    private fun renderStats(stats: AlbumStats) {
        binding.txtPercent.text = getString(R.string.stats_percent_value, stats.completionPercent)

        bindStat(binding.cardObtained.root, stats.obtained.toString(), getString(R.string.dashboard_obtained))
        bindStat(binding.cardMissing.root, stats.missing.toString(), getString(R.string.dashboard_missing))
        bindStat(binding.cardRepeated.root, stats.repeated.toString(), getString(R.string.dashboard_repeated))
        bindStat(binding.cardTotal.root, stats.total.toString(), getString(R.string.stats_total))

        ObjectAnimator.ofInt(binding.progressTotal, "progress", 0, stats.completionPercent).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun bindStat(root: View, value: String, label: String) {
        root.findViewById<android.widget.TextView>(R.id.txtValue).text = value
        root.findViewById<android.widget.TextView>(R.id.txtLabel).text = label
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
