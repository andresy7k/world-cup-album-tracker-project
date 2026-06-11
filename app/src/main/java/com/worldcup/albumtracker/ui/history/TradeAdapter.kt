package com.worldcup.albumtracker.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.worldcup.albumtracker.data.model.Trade
import com.worldcup.albumtracker.databinding.ItemTradeBinding

/**
 * Adapter del historial de intercambios. Muestra la lámina entregada,
 * la recibida y la fecha. Permite eliminar un registro con el icono de borrar.
 */
class TradeAdapter(
    private val onDelete: (Trade) -> Unit
) : ListAdapter<Trade, TradeAdapter.TradeViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeViewHolder {
        val binding = ItemTradeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TradeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TradeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TradeViewHolder(
        private val binding: ItemTradeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trade: Trade) {
            binding.txtGiven.text = trade.stickerGiven
            binding.txtReceived.text = trade.stickerReceived
            binding.txtDate.text = trade.tradeDate
            binding.btnDelete.setOnClickListener { onDelete(trade) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Trade>() {
            override fun areItemsTheSame(oldItem: Trade, newItem: Trade) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Trade, newItem: Trade) =
                oldItem == newItem
        }
    }
}
