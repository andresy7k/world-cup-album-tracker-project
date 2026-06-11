package com.worldcup.albumtracker.ui.trade

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.data.repository.TradeOutcome
import com.worldcup.albumtracker.databinding.ActivityTradeBinding
import com.worldcup.albumtracker.utils.app
import com.worldcup.albumtracker.utils.ViewModelFactory

/**
 * Pantalla para registrar un intercambio de láminas.
 *
 * Flujo: el usuario escribe el número de la lámina que entrega (repetida) y
 * el de la que recibe; mediante el control "swipe to confirm" se valida y
 * registra el intercambio. Los errores se muestran como Snackbar.
 */
class TradeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradeBinding

    private val viewModel: TradeViewModel by viewModels {
        ViewModelFactory(app.albumRepository, app.playerRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTradeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.swipeConfirm.setLabel(getString(R.string.swipe_to_confirm_trade))
        binding.swipeConfirm.onConfirmed = { attemptTrade() }

        observeViewModel()
    }

    private fun attemptTrade() {
        val given = binding.inputGiven.text?.toString()?.toIntOrNull()
        val received = binding.inputReceived.text?.toString()?.toIntOrNull()
        if (given == null || received == null) {
            Snackbar.make(binding.root, R.string.trade_invalid_numbers, Snackbar.LENGTH_SHORT).show()
            binding.swipeConfirm.reset()
            return
        }
        viewModel.trade(given, received)
    }

    private fun observeViewModel() {
        viewModel.result.observe(this) { outcome ->
            outcome ?: return@observe
            when (outcome) {
                is TradeOutcome.Success -> {
                    Snackbar.make(binding.root, R.string.trade_success, Snackbar.LENGTH_LONG).show()
                    binding.root.postDelayed({ finish() }, 900)
                }
                is TradeOutcome.Error -> {
                    Snackbar.make(binding.root, outcome.message, Snackbar.LENGTH_LONG).show()
                    binding.swipeConfirm.reset()
                }
            }
            viewModel.clearResult()
        }
    }
}
