package com.worldcup.albumtracker.ui.register

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.data.model.Sticker
import com.worldcup.albumtracker.databinding.ActivityRegisterBinding
import com.worldcup.albumtracker.utils.app
import com.worldcup.albumtracker.utils.ViewModelFactory

/**
 * Pantalla para registrar una lámina como obtenida.
 *
 * Flujo: el usuario escribe (o recibe precargado) el número de lámina,
 * la app muestra su información y, mediante el control "swipe to confirm",
 * confirma el registro. Si ya la tenía, se suma a repetidas.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory(app.albumRepository, app.playerRepository)
    }

    private var currentNumber: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.swipeConfirm.setLabel(getString(R.string.swipe_to_register))
        binding.swipeConfirm.onConfirmed = {
            currentNumber?.let { viewModel.register(it) }
        }

        binding.btnLookup.setOnClickListener { lookup() }

        // Número precargado al venir desde el álbum.
        intent.getIntExtra(EXTRA_STICKER_NUMBER, -1).takeIf { it > 0 }?.let { number ->
            binding.inputNumber.setText(number.toString())
            lookup()
        }

        observeViewModel()
    }

    private fun lookup() {
        val number = binding.inputNumber.text?.toString()?.toIntOrNull()
        if (number == null) {
            Snackbar.make(binding.root, R.string.register_invalid_number, Snackbar.LENGTH_SHORT).show()
            return
        }
        viewModel.lookup(number)
    }

    private fun observeViewModel() {
        viewModel.sticker.observe(this) { sticker ->
            if (sticker == null) {
                binding.cardPreview.visibility = View.GONE
                binding.swipeConfirm.visibility = View.GONE
                binding.txtNotFound.visibility = View.VISIBLE
                currentNumber = null
            } else {
                renderPreview(sticker)
            }
        }

        viewModel.registered.observe(this) { result ->
            result ?: return@observe
            val message = if (result.wasRepeated) {
                getString(R.string.register_added_repeated, result.sticker.number)
            } else {
                getString(R.string.register_added_new, result.sticker.number)
            }
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            // Cierra tras un breve instante para que se vea el feedback.
            binding.root.postDelayed({ finish() }, 900)
        }
    }

    private fun renderPreview(sticker: Sticker) {
        currentNumber = sticker.number
        binding.txtNotFound.visibility = View.GONE
        binding.cardPreview.visibility = View.VISIBLE
        binding.swipeConfirm.visibility = View.VISIBLE
        binding.swipeConfirm.reset()

        binding.txtPreviewNumber.text = getString(R.string.sticker_number, sticker.number)
        binding.txtPreviewPlayer.text = sticker.playerName
        binding.txtPreviewCountry.text = sticker.country

        val statusRes = if (sticker.obtained) R.string.register_status_owned else R.string.register_status_new
        binding.txtPreviewStatus.text = getString(statusRes)
        val statusColor = if (sticker.obtained) R.color.warning else R.color.success
        binding.txtPreviewStatus.setTextColor(getColor(statusColor))
    }

    companion object {
        const val EXTRA_STICKER_NUMBER = "extra_sticker_number"
    }
}
