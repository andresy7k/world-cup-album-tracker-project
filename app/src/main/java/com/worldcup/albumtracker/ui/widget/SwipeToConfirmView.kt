package com.worldcup.albumtracker.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.worldcup.albumtracker.R

/**
 * Control "Swipe to confirm" reutilizable.
 *
 * El usuario arrastra un pulgar (thumb) de izquierda a derecha; al superar
 * el 85% del recorrido se dispara [onConfirmed]. Aporta una microinteracción
 * premium para confirmar acciones críticas (registrar lámina / intercambio).
 */
class SwipeToConfirmView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val thumb: View
    private val label: android.widget.TextView

    private var downX = 0f
    private var thumbStartX = 0f
    private var confirmed = false

    /** Callback invocado una sola vez cuando el deslizamiento se completa. */
    var onConfirmed: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_swipe_to_confirm, this, true)
        thumb = findViewById(R.id.swipeThumb)
        label = findViewById(R.id.swipeLabel)
    }

    /** Permite cambiar el texto mostrado en el control. */
    fun setLabel(text: CharSequence) {
        label.text = text
    }

    /** Reinicia el control a su estado inicial (por si se reutiliza la pantalla). */
    fun reset() {
        confirmed = false
        thumb.animate().x(paddingStart.toFloat()).setDuration(200).start()
        label.alpha = 1f
        isEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (confirmed || !isEnabled) return false

        val maxX = (width - thumb.width - paddingEnd).toFloat()
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                thumbStartX = thumb.x
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val delta = event.x - downX
                val newX = (thumbStartX + delta).coerceIn(paddingStart.toFloat(), maxX)
                thumb.x = newX
                // Desvanece el texto a medida que avanza el pulgar.
                label.alpha = 1f - (newX / maxX).coerceIn(0f, 1f)
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (thumb.x >= maxX * 0.85f) {
                    confirmed = true
                    isEnabled = false
                    thumb.animate().x(maxX).setDuration(150).withEndAction {
                        performHaptic()
                        onConfirmed?.invoke()
                    }.start()
                } else {
                    // Vuelve al inicio si no completó el recorrido.
                    thumb.animate().x(paddingStart.toFloat()).setDuration(200).start()
                    label.animate().alpha(1f).setDuration(200).start()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun performHaptic() {
        try {
            performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
        } catch (_: Exception) {
            performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }
}
