package com.worldcup.albumtracker.data.repository

import com.worldcup.albumtracker.data.database.AlbumDbHelper
import com.worldcup.albumtracker.data.model.AlbumStats
import com.worldcup.albumtracker.data.model.CountryProgress
import com.worldcup.albumtracker.data.model.Sticker
import com.worldcup.albumtracker.data.model.Trade

/**
 * Single source of truth for sticker / trade data.
 *
 * The repository hides the persistence mechanism (SQLite) from the rest of the
 * app, following the Repository Pattern. ViewModels depend only on this class,
 * never on [AlbumDbHelper] directly.
 */
class AlbumRepository(private val dbHelper: AlbumDbHelper) {

    // ---------------------------------------------------------------------
    // Stickers
    // ---------------------------------------------------------------------

    fun getAllStickers(): List<Sticker> = dbHelper.getAllStickers()

    fun getObtainedStickers(): List<Sticker> = dbHelper.getObtainedStickers()

    fun getMissingStickers(): List<Sticker> = dbHelper.getMissingStickers()

    fun getRepeatedStickers(): List<Sticker> = dbHelper.getRepeatedStickers()

    fun getStickersByCountry(country: String): List<Sticker> =
        dbHelper.getStickersByCountry(country)

    fun searchStickers(query: String): List<Sticker> = dbHelper.searchStickers(query)

    fun getStickerByNumber(number: Int): Sticker? = dbHelper.getStickerByNumber(number)

    fun getRecentObtained(limit: Int = 5): List<Sticker> = dbHelper.getRecentObtained(limit)

    fun getCountries(): List<String> = dbHelper.getCountries()

    /**
     * Registers a sticker as obtained. If it is already owned, the repeated
     * counter is incremented instead (handled inside the DB helper).
     */
    fun registerSticker(number: Int): Boolean = dbHelper.registerSticker(number)

    fun resetAlbum() = dbHelper.resetAlbum()

    // ---------------------------------------------------------------------
    // Trades
    // ---------------------------------------------------------------------

    fun getAllTrades(): List<Trade> = dbHelper.getAllTrades()

    /**
     * Performs a full trade operation:
     *  1. Validates that the sticker given away actually has repeated copies.
     *  2. Decreases the repeated inventory of the sticker given away.
     *  3. Automatically marks the received sticker as obtained.
     *  4. Stores a human-readable record in the trade history.
     *
     * @return a [TradeOutcome] describing success or the reason of failure.
     */
    fun registerTrade(givenNumber: Int, receivedNumber: Int): TradeOutcome {
        val given = dbHelper.getStickerByNumber(givenNumber)
            ?: return TradeOutcome.Error("La lámina entregada no existe")
        val received = dbHelper.getStickerByNumber(receivedNumber)
            ?: return TradeOutcome.Error("La lámina recibida no existe")

        if (givenNumber == receivedNumber) {
            return TradeOutcome.Error("No puedes intercambiar una lámina por sí misma")
        }
        if (given.repeatedCount <= 0) {
            return TradeOutcome.Error("No tienes repetidas de la lámina N° $givenNumber")
        }

        // 2. Decrease repeated inventory of the given sticker.
        dbHelper.decreaseRepeated(givenNumber)

        // 3. Auto-mark received sticker as obtained (increments repeated if owned).
        dbHelper.registerSticker(receivedNumber)

        // 4. Store readable history entry.
        val givenLabel = "N° ${given.number} - ${given.playerName}"
        val receivedLabel = "N° ${received.number} - ${received.playerName}"
        dbHelper.insertTrade(givenLabel, receivedLabel)

        return TradeOutcome.Success
    }

    fun deleteTrade(tradeId: Long): Boolean = dbHelper.deleteTrade(tradeId)

    // ---------------------------------------------------------------------
    // Statistics
    // ---------------------------------------------------------------------

    /** Computes the aggregate album statistics for the dashboard / stats screen. */
    fun getAlbumStats(): AlbumStats {
        val all = dbHelper.getAllStickers()
        val total = all.size
        val obtained = all.count { it.obtained }
        val missing = total - obtained
        val repeated = all.sumOf { it.repeatedCount }
        val percent = if (total == 0) 0 else (obtained * 100) / total
        val topCountries = getCountryProgress().take(5)
        return AlbumStats(
            total = total,
            obtained = obtained,
            missing = missing,
            repeated = repeated,
            completionPercent = percent,
            topCountries = topCountries
        )
    }

    fun getCountryProgress(): List<CountryProgress> = dbHelper.getCountryProgress()
}

/** Result of a trade attempt, used by the UI to show feedback. */
sealed class TradeOutcome {
    object Success : TradeOutcome()
    data class Error(val message: String) : TradeOutcome()
}
