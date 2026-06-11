package com.worldcup.albumtracker.ui.dashboard

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.data.model.AlbumStats
import com.worldcup.albumtracker.databinding.FragmentDashboardBinding
import com.worldcup.albumtracker.ui.history.HistoryActivity
import com.worldcup.albumtracker.ui.register.RegisterActivity
import com.worldcup.albumtracker.ui.trade.TradeActivity
import com.worldcup.albumtracker.utils.viewModelFactory

/**
 * Dashboard: shows aggregate album statistics, an animated progress bar,
 * quick actions and the most recently obtained stickers.
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels { viewModelFactory() }

    private val recentAdapter = RecentStickerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerRecent.adapter = recentAdapter

        // Quick action navigation.
        binding.cardRegister.setOnClickListener {
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
        }
        binding.cardTrade.setOnClickListener {
            startActivity(Intent(requireContext(), TradeActivity::class.java))
        }
        binding.cardHistory.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.stats.observe(viewLifecycleOwner) { renderStats(it) }
        viewModel.recent.observe(viewLifecycleOwner) { stickers ->
            recentAdapter.submitList(stickers)
            binding.txtEmptyRecent.visibility = if (stickers.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerRecent.visibility = if (stickers.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun renderStats(stats: AlbumStats) {
        binding.txtPercent.text = getString(R.string.dashboard_completed, stats.completionPercent)
            .replace(" completado", "")
        binding.txtProgressDetail.text =
            "${stats.obtained} obtenidas · ${stats.missing} pendientes"

        // Animate the progress bar from 0 to the real value.
        ObjectAnimator.ofInt(binding.progressAlbum, "progress", 0, stats.completionPercent).apply {
            duration = 900
            interpolator = DecelerateInterpolator()
            start()
        }

        // Stat cards.
        bindStat(binding.statObtained.root, stats.obtained.toString(), getString(R.string.dashboard_obtained))
        bindStat(binding.statMissing.root, stats.missing.toString(), getString(R.string.dashboard_missing))
        bindStat(binding.statRepeated.root, stats.repeated.toString(), getString(R.string.dashboard_repeated))
    }

    private fun bindStat(root: View, value: String, label: String) {
        root.findViewById<android.widget.TextView>(R.id.txtStatValue).text = value
        root.findViewById<android.widget.TextView>(R.id.txtStatLabel).text = label
    }

    override fun onResume() {
        super.onResume()
        // Reload so changes from register/trade flows are reflected immediately.
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
