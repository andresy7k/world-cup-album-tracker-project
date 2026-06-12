package com.worldcup.albumtracker.ui.history

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.data.model.Trade
import com.worldcup.albumtracker.databinding.ActivityHistoryBinding
import com.worldcup.albumtracker.utils.app

/**
 * Historial de intercambios realizados.
 *
 * Lee los intercambios del repositorio y permite eliminarlos. No requiere un
 * ViewModel propio porque la operación es simple y sincrónica sobre SQLite.
 */
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    private val adapter = TradeAdapter(onDelete = ::confirmDelete)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.recyclerTrades.adapter = adapter

        loadTrades()
    }

    private fun loadTrades() {
        val trades = app.albumRepository.getAllTrades()
        adapter.submitList(trades)
        val empty = trades.isEmpty()
        binding.emptyState.root.visibility = if (empty) View.VISIBLE else View.GONE
        binding.recyclerTrades.visibility = if (empty) View.GONE else View.VISIBLE
    }

    private fun confirmDelete(trade: Trade) {
        if (app.albumRepository.deleteTrade(trade.id)) {
            Snackbar.make(binding.root, R.string.trade_deleted, Snackbar.LENGTH_SHORT).show()
            loadTrades()
        }
    }
}
