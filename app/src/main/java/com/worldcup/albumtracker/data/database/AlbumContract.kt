package com.worldcup.albumtracker.data.database

import android.provider.BaseColumns

/**
 * Contrato de la base de datos: define nombres de tablas y columnas
 * para evitar literales repartidos por el código (Clean Code).
 */
object AlbumContract {

    const val DATABASE_NAME = "album_tracker.db"
    const val DATABASE_VERSION = 1

    /** Tabla de láminas. */
    object StickerEntry : BaseColumns {
        const val TABLE_NAME = "stickers"
        const val COLUMN_NUMBER = "number"
        const val COLUMN_PLAYER_NAME = "playerName"
        const val COLUMN_COUNTRY = "country"
        const val COLUMN_OBTAINED = "obtained"
        const val COLUMN_REPEATED_COUNT = "repeatedCount"
    }

    /** Tabla de intercambios. */
    object TradeEntry : BaseColumns {
        const val TABLE_NAME = "trades"
        const val COLUMN_STICKER_GIVEN = "stickerGiven"
        const val COLUMN_STICKER_RECEIVED = "stickerReceived"
        const val COLUMN_TRADE_DATE = "tradeDate"
    }
}
