package com.worldcup.albumtracker.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.worldcup.albumtracker.databinding.ActivitySplashBinding
import com.worldcup.albumtracker.ui.MainActivity

/**
 * Premium splash screen.
 *
 * Sequence (≈2.5s total):
 *  1. Dark gradient background (window background from theme).
 *  2. Logo pops in (scale + fade).
 *  3. App name fades up.
 *  4. Subtle loading indicator appears.
 *  5. Elegant transition to the Dashboard (MainActivity).
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val splashDuration = 2500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimations()

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMain()
        }, splashDuration)
    }

    private fun playAnimations() {
        val logoAnim = AnimationUtils.loadAnimation(this, com.worldcup.albumtracker.R.anim.logo_pop_in)
        val textAnim = AnimationUtils.loadAnimation(this, com.worldcup.albumtracker.R.anim.text_fade_up)

        binding.imgLogo.startAnimation(logoAnim)

        // Stagger the text + tagline slightly after the logo.
        binding.txtAppName.postDelayed({
            binding.txtAppName.startAnimation(textAnim)
            binding.txtTagline.startAnimation(textAnim)
        }, 600)

        // Reveal the loading indicator near the end.
        binding.progressSplash.postDelayed({
            binding.progressSplash.visibility = android.view.View.VISIBLE
        }, 1400)
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        // Elegant crossfade transition.
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
