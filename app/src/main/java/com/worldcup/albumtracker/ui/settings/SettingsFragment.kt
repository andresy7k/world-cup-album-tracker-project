package com.worldcup.albumtracker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.databinding.FragmentSettingsBinding
import com.worldcup.albumtracker.utils.app
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope

/**
 * Pantalla de Ajustes: información de la app y opción para reiniciar el
 * álbum (restablece los datos de prueba en SQLite).
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardReset.setOnClickListener { confirmReset() }
        binding.cardAbout.setOnClickListener { showAbout() }
    }

    private fun confirmReset() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.settings_reset)
            .setMessage(R.string.settings_reset_confirm)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_confirm) { _, _ -> resetAlbum() }
            .show()
    }

    private fun resetAlbum() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { app.albumRepository.resetAlbum() }
            Snackbar.make(binding.root, R.string.settings_reset_done, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showAbout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.app_name)
            .setMessage(R.string.settings_about_text)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
