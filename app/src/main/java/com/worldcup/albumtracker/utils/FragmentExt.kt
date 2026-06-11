package com.worldcup.albumtracker.utils

import androidx.fragment.app.Fragment
import com.worldcup.albumtracker.AlbumApplication

/**
 * Convenience accessor so fragments can reach the app-level Service Locator
 * and build a [ViewModelFactory] without repeating boilerplate.
 */
val Fragment.app: AlbumApplication
    get() = requireActivity().application as AlbumApplication

fun Fragment.viewModelFactory(): ViewModelFactory =
    ViewModelFactory(app.albumRepository, app.playerRepository)
