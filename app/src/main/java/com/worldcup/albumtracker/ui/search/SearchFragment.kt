package com.worldcup.albumtracker.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.data.model.Player
import com.worldcup.albumtracker.databinding.FragmentSearchBinding
import com.worldcup.albumtracker.utils.ErrorType
import com.worldcup.albumtracker.utils.UiState
import com.worldcup.albumtracker.utils.viewModelFactory

/**
 * Buscador de jugadores que consume la API de TheSportsDB.
 *
 * Maneja de forma explícita todos los estados requeridos: cargando,
 * éxito, sin resultados, sin internet, timeout y error de servidor.
 */
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels { viewModelFactory() }

    private val adapter = PlayerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerPlayers.adapter = adapter

        binding.btnSearch.setOnClickListener { performSearch() }
        binding.inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else false
        }
        binding.btnRetry.setOnClickListener { performSearch() }

        observeViewModel()
    }

    private fun performSearch() {
        val query = binding.inputSearch.text?.toString().orEmpty()
        if (query.isBlank()) return
        hideKeyboard()
        viewModel.search(query)
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> renderLoading()
                is UiState.Success -> renderSuccess(state.data)
                is UiState.Empty -> renderMessage(
                    R.drawable.ic_search,
                    getString(R.string.error_no_results),
                    showRetry = false
                )
                is UiState.Error -> renderError(state.type, state.message)
            }
        }
    }

    private fun renderLoading() {
        binding.progressSearch.visibility = View.VISIBLE
        binding.recyclerPlayers.visibility = View.GONE
        binding.stateContainer.visibility = View.GONE
    }

    private fun renderSuccess(players: List<Player>) {
        binding.progressSearch.visibility = View.GONE
        binding.stateContainer.visibility = View.GONE
        binding.recyclerPlayers.visibility = View.VISIBLE
        adapter.submitList(players)
    }

    private fun renderError(type: ErrorType, message: String) {
        val icon = when (type) {
            ErrorType.NO_INTERNET -> R.drawable.ic_search
            ErrorType.TIMEOUT -> R.drawable.ic_trending
            else -> R.drawable.ic_search
        }
        renderMessage(icon, message, showRetry = true)
    }

    private fun renderMessage(iconRes: Int, message: String, showRetry: Boolean) {
        binding.progressSearch.visibility = View.GONE
        binding.recyclerPlayers.visibility = View.GONE
        binding.stateContainer.visibility = View.VISIBLE
        binding.imgState.setImageResource(iconRes)
        binding.txtState.text = message
        binding.btnRetry.visibility = if (showRetry) View.VISIBLE else View.GONE
    }

    private fun hideKeyboard() {
        val imm = requireContext()
            .getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputSearch.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
