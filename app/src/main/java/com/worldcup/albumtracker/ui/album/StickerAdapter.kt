package com.worldcup.albumtracker.ui.album

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.data.model.Sticker
import com.worldcup.albumtracker.databinding.ItemStickerBinding

/**
 * Grid adapter for stickers. Visually differentiates obtained vs missing
 * stickers and shows a "repeated" badge when applicable.
 *
 * @param onClick optional click callback (used by the album to register a sticker).
 */
class StickerAdapter(
    private val onClick: ((Sticker) -> Unit)? = null
) : ListAdapter<Sticker, StickerAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemStickerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemStickerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val sticker = getItem(position)
        val ctx = holder.itemView.context
        with(holder.binding) {
            txtNumber.text = sticker.number.toString()
            txtPlayer.text = sticker.playerName
            txtCountry.text = sticker.country

            if (sticker.obtained) {
                photoArea.setBackgroundResource(R.drawable.bg_sticker_obtained)
                txtPlayer.alpha = 1f
                txtNumber.alpha = 1f
            } else {
                photoArea.setBackgroundResource(R.drawable.bg_sticker_missing)
                txtPlayer.alpha = 0.5f
                txtNumber.alpha = 0.4f
            }

            if (sticker.repeatedCount > 0) {
                txtRepeated.visibility = View.VISIBLE
                txtRepeated.text = ctx.getString(R.string.sticker_repeated_count, sticker.repeatedCount)
                    .replace(" repetidas", "")
            } else {
                txtRepeated.visibility = View.GONE
            }

            root.setOnClickListener { onClick?.invoke(sticker) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Sticker>() {
            override fun areItemsTheSame(oldItem: Sticker, newItem: Sticker) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Sticker, newItem: Sticker) =
                oldItem == newItem
        }
    }
}
