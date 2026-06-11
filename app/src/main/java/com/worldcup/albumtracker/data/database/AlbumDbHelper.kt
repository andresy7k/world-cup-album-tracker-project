package com.worldcup.albumtracker.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.worldcup.albumtracker.data.database.AlbumContract.StickerEntry
import com.worldcup.albumtracker.data.database.AlbumContract.TradeEntry
import com.worldcup.albumtracker.data.model.CountryProgress
import com.worldcup.albumtracker.data.model.Sticker
import com.worldcup.albumtracker.data.model.Trade
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper de SQLite que gestiona la creación del esquema, los datos
 * iniciales de prueba y todas las operaciones CRUD del álbum.
 *
 * Implementado con SQLiteOpenHelper puro (sin Room) según requisitos.
 */
class AlbumDbHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    AlbumContract.DATABASE_NAME,
    null,
    AlbumContract.DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_STICKERS)
        db.execSQL(SQL_CREATE_TRADES)
        seedStickers(db)
        seedTrades(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${StickerEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${TradeEntry.TABLE_NAME}")
        onCreate(db)
    }

    // --------------------------------------------------------------------
    // SEED / DATOS INICIALES
    // --------------------------------------------------------------------

    private fun seedStickers(db: SQLiteDatabase) {
        // Cada selección aporta varias láminas; algunas obtenidas y con repetidas
        // para que la app tenga datos de prueba realistas desde el primer uso.
        val seed = listOf(
            // Argentina
            SeedSticker(1, "Lionel Messi", "Argentina", true, 2),
            SeedSticker(2, "Julián Álvarez", "Argentina", true, 0),
            SeedSticker(3, "Emiliano Martínez", "Argentina", true, 1),
            SeedSticker(4, "Enzo Fernández", "Argentina", false, 0),
            SeedSticker(5, "Rodrigo De Paul", "Argentina", false, 0),
            // Brasil
            SeedSticker(6, "Vinícius Júnior", "Brasil", true, 1),
            SeedSticker(7, "Rodrygo", "Brasil", true, 0),
            SeedSticker(8, "Casemiro", "Brasil", false, 0),
            SeedSticker(9, "Marquinhos", "Brasil", false, 0),
            SeedSticker(10, "Alisson Becker", "Brasil", true, 3),
            // España
            SeedSticker(11, "Lamine Yamal", "España", true, 0),
            SeedSticker(12, "Pedri", "España", true, 1),
            SeedSticker(13, "Rodri", "España", false, 0),
            SeedSticker(14, "Álvaro Morata", "España", false, 0),
            SeedSticker(15, "Unai Simón", "España", true, 0),
            // Francia
            SeedSticker(16, "Kylian Mbappé", "Francia", true, 2),
            SeedSticker(17, "Antoine Griezmann", "Francia", true, 0),
            SeedSticker(18, "Aurélien Tchouaméni", "Francia", false, 0),
            SeedSticker(19, "Ousmane Dembélé", "Francia", false, 0),
            SeedSticker(20, "Mike Maignan", "Francia", true, 1),
            // Alemania
            SeedSticker(21, "Jamal Musiala", "Alemania", true, 0),
            SeedSticker(22, "Florian Wirtz", "Alemania", true, 1),
            SeedSticker(23, "Joshua Kimmich", "Alemania", false, 0),
            SeedSticker(24, "Kai Havertz", "Alemania", false, 0),
            SeedSticker(25, "Manuel Neuer", "Alemania", true, 0),
            // Colombia
            SeedSticker(26, "Luis Díaz", "Colombia", true, 1),
            SeedSticker(27, "James Rodríguez", "Colombia", true, 0),
            SeedSticker(28, "Jhon Durán", "Colombia", false, 0),
            SeedSticker(29, "Richard Ríos", "Colombia", false, 0),
            SeedSticker(30, "Camilo Vargas", "Colombia", false, 0),
            // México
            SeedSticker(31, "Santiago Giménez", "México", true, 0),
            SeedSticker(32, "Edson Álvarez", "México", true, 2),
            SeedSticker(33, "Hirving Lozano", "México", false, 0),
            SeedSticker(34, "Guillermo Ochoa", "México", false, 0),
            SeedSticker(35, "César Montes", "México", false, 0),
            // Portugal
            SeedSticker(36, "Cristiano Ronaldo", "Portugal", true, 1),
            SeedSticker(37, "Bruno Fernandes", "Portugal", true, 0),
            SeedSticker(38, "Bernardo Silva", "Portugal", false, 0),
            SeedSticker(39, "Rafael Leão", "Portugal", false, 0),
            SeedSticker(40, "Diogo Costa", "Portugal", false, 0),
            // Inglaterra
            SeedSticker(41, "Jude Bellingham", "Inglaterra", true, 1),
            SeedSticker(42, "Harry Kane", "Inglaterra", true, 0),
            SeedSticker(43, "Bukayo Saka", "Inglaterra", false, 0),
            SeedSticker(44, "Phil Foden", "Inglaterra", false, 0),
            SeedSticker(45, "Jordan Pickford", "Inglaterra", false, 0)
        )

        db.beginTransaction()
        try {
            seed.forEach { s ->
                val values = ContentValues().apply {
                    put(StickerEntry.COLUMN_NUMBER, s.number)
                    put(StickerEntry.COLUMN_PLAYER_NAME, s.player)
                    put(StickerEntry.COLUMN_COUNTRY, s.country)
                    put(StickerEntry.COLUMN_OBTAINED, if (s.obtained) 1 else 0)
                    put(StickerEntry.COLUMN_REPEATED_COUNT, s.repeated)
                }
                db.insert(StickerEntry.TABLE_NAME, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun seedTrades(db: SQLiteDatabase) {
        val sample = listOf(
            Triple("N° 5 - Rodrigo De Paul", "N° 1 - Lionel Messi", currentDate()),
            Triple("N° 24 - Kai Havertz", "N° 16 - Kylian Mbappé", currentDate())
        )
        sample.forEach { (given, received, date) ->
            val values = ContentValues().apply {
                put(TradeEntry.COLUMN_STICKER_GIVEN, given)
                put(TradeEntry.COLUMN_STICKER_RECEIVED, received)
                put(TradeEntry.COLUMN_TRADE_DATE, date)
            }
            db.insert(TradeEntry.TABLE_NAME, null, values)
        }
    }

    // --------------------------------------------------------------------
    // CRUD - STICKERS
    // --------------------------------------------------------------------

    /** Devuelve todas las láminas ordenadas por número. */
    fun getAllStickers(): List<Sticker> = queryStickers(null, null)

    /** Devuelve solo las láminas obtenidas. */
    fun getObtainedStickers(): List<Sticker> =
        queryStickers("${StickerEntry.COLUMN_OBTAINED} = ?", arrayOf("1"))

    /** Devuelve solo las láminas pendientes (no obtenidas). */
    fun getMissingStickers(): List<Sticker> =
        queryStickers("${StickerEntry.COLUMN_OBTAINED} = ?", arrayOf("0"))

    /** Devuelve las láminas con repetidas (repeatedCount > 0). */
    fun getRepeatedStickers(): List<Sticker> =
        queryStickers("${StickerEntry.COLUMN_REPEATED_COUNT} > ?", arrayOf("0"))

    /** Devuelve las últimas láminas obtenidas (para el Dashboard). */
    fun getRecentObtained(limit: Int = 5): List<Sticker> {
        val db = readableDatabase
        val cursor = db.query(
            StickerEntry.TABLE_NAME,
            null,
            "${StickerEntry.COLUMN_OBTAINED} = ?",
            arrayOf("1"),
            null, null,
            "${BaseColumnId} DESC",
            limit.toString()
        )
        return cursor.toStickerList()
    }

    /** Búsqueda por número, nombre de jugador o país. */
    fun searchStickers(query: String): List<Sticker> {
        val like = "%$query%"
        val selection = "${StickerEntry.COLUMN_NUMBER} LIKE ? OR " +
                "${StickerEntry.COLUMN_PLAYER_NAME} LIKE ? OR " +
                "${StickerEntry.COLUMN_COUNTRY} LIKE ?"
        return queryStickers(selection, arrayOf(like, like, like))
    }

    /** Filtra las láminas por selección nacional. */
    fun getStickersByCountry(country: String): List<Sticker> =
        queryStickers("${StickerEntry.COLUMN_COUNTRY} = ?", arrayOf(country))

    /** Lista de selecciones distintas (para el filtro). */
    fun getCountries(): List<String> {
        val db = readableDatabase
        val cursor = db.query(
            true,
            StickerEntry.TABLE_NAME,
            arrayOf(StickerEntry.COLUMN_COUNTRY),
            null, null, null, null,
            "${StickerEntry.COLUMN_COUNTRY} ASC",
            null
        )
        val result = mutableListOf<String>()
        cursor.use {
            while (it.moveToNext()) {
                result.add(it.getString(0))
            }
        }
        return result
    }

    /**
     * Marca una lámina como obtenida. Si ya estaba obtenida, incrementa el
     * contador de repetidas. Devuelve true si la operación tuvo éxito.
     */
    fun registerSticker(number: Int): Boolean {
        val db = writableDatabase
        val current = getStickerByNumber(number) ?: return false
        val values = ContentValues()
        if (current.obtained) {
            values.put(StickerEntry.COLUMN_REPEATED_COUNT, current.repeatedCount + 1)
        } else {
            values.put(StickerEntry.COLUMN_OBTAINED, 1)
        }
        val rows = db.update(
            StickerEntry.TABLE_NAME,
            values,
            "${StickerEntry.COLUMN_NUMBER} = ?",
            arrayOf(number.toString())
        )
        return rows > 0
    }

    /** Inserta una lámina nueva (CRUD completo - create). */
    fun insertSticker(sticker: Sticker): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(StickerEntry.COLUMN_NUMBER, sticker.number)
            put(StickerEntry.COLUMN_PLAYER_NAME, sticker.playerName)
            put(StickerEntry.COLUMN_COUNTRY, sticker.country)
            put(StickerEntry.COLUMN_OBTAINED, if (sticker.obtained) 1 else 0)
            put(StickerEntry.COLUMN_REPEATED_COUNT, sticker.repeatedCount)
        }
        return db.insert(StickerEntry.TABLE_NAME, null, values)
    }

    /** Obtiene una lámina por su número. */
    fun getStickerByNumber(number: Int): Sticker? {
        val list = queryStickers(
            "${StickerEntry.COLUMN_NUMBER} = ?",
            arrayOf(number.toString())
        )
        return list.firstOrNull()
    }

    /** Reduce en 1 las repetidas de una lámina (usado al intercambiar). */
    fun decreaseRepeated(number: Int): Boolean {
        val sticker = getStickerByNumber(number) ?: return false
        if (sticker.repeatedCount <= 0) return false
        val db = writableDatabase
        val values = ContentValues().apply {
            put(StickerEntry.COLUMN_REPEATED_COUNT, sticker.repeatedCount - 1)
        }
        val rows = db.update(
            StickerEntry.TABLE_NAME,
            values,
            "${StickerEntry.COLUMN_NUMBER} = ?",
            arrayOf(number.toString())
        )
        return rows > 0
    }

    /** Borra todas las láminas y restablece la siembra inicial. */
    fun resetAlbum() {
        val db = writableDatabase
        db.delete(StickerEntry.TABLE_NAME, null, null)
        db.delete(TradeEntry.TABLE_NAME, null, null)
        seedStickers(db)
        seedTrades(db)
    }

    // --------------------------------------------------------------------
    // CRUD - TRADES
    // --------------------------------------------------------------------

    fun getAllTrades(): List<Trade> {
        val db = readableDatabase
        val cursor = db.query(
            TradeEntry.TABLE_NAME,
            null, null, null, null, null,
            "$BaseColumnId DESC"
        )
        val result = mutableListOf<Trade>()
        cursor.use {
            while (it.moveToNext()) {
                result.add(
                    Trade(
                        id = it.getLong(it.getColumnIndexOrThrow(BaseColumnId)),
                        stickerGiven = it.getString(it.getColumnIndexOrThrow(TradeEntry.COLUMN_STICKER_GIVEN)),
                        stickerReceived = it.getString(it.getColumnIndexOrThrow(TradeEntry.COLUMN_STICKER_RECEIVED)),
                        tradeDate = it.getString(it.getColumnIndexOrThrow(TradeEntry.COLUMN_TRADE_DATE))
                    )
                )
            }
        }
        return result
    }

    fun insertTrade(given: String, received: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TradeEntry.COLUMN_STICKER_GIVEN, given)
            put(TradeEntry.COLUMN_STICKER_RECEIVED, received)
            put(TradeEntry.COLUMN_TRADE_DATE, currentDate())
        }
        return db.insert(TradeEntry.TABLE_NAME, null, values)
    }

    fun deleteTrade(id: Long): Boolean {
        val db = writableDatabase
        return db.delete(TradeEntry.TABLE_NAME, "$BaseColumnId = ?", arrayOf(id.toString())) > 0
    }

    // --------------------------------------------------------------------
    // ESTADÍSTICAS
    // --------------------------------------------------------------------

    fun getCountryProgress(): List<CountryProgress> {
        val all = getAllStickers()
        return all.groupBy { it.country }
            .map { (country, list) ->
                CountryProgress(
                    country = country,
                    obtained = list.count { it.obtained },
                    total = list.size
                )
            }
            .sortedByDescending { it.percent }
    }

    // --------------------------------------------------------------------
    // HELPERS PRIVADOS
    // --------------------------------------------------------------------

    private fun queryStickers(selection: String?, args: Array<String>?): List<Sticker> {
        val db = readableDatabase
        val cursor = db.query(
            StickerEntry.TABLE_NAME,
            null,
            selection,
            args,
            null, null,
            "${StickerEntry.COLUMN_NUMBER} ASC"
        )
        return cursor.toStickerList()
    }

    private fun Cursor.toStickerList(): List<Sticker> {
        val result = mutableListOf<Sticker>()
        use {
            while (it.moveToNext()) {
                result.add(
                    Sticker(
                        id = it.getLong(it.getColumnIndexOrThrow(BaseColumnId)),
                        number = it.getInt(it.getColumnIndexOrThrow(StickerEntry.COLUMN_NUMBER)),
                        playerName = it.getString(it.getColumnIndexOrThrow(StickerEntry.COLUMN_PLAYER_NAME)),
                        country = it.getString(it.getColumnIndexOrThrow(StickerEntry.COLUMN_COUNTRY)),
                        obtained = it.getInt(it.getColumnIndexOrThrow(StickerEntry.COLUMN_OBTAINED)) == 1,
                        repeatedCount = it.getInt(it.getColumnIndexOrThrow(StickerEntry.COLUMN_REPEATED_COUNT))
                    )
                )
            }
        }
        return result
    }

    private fun currentDate(): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

    private data class SeedSticker(
        val number: Int,
        val player: String,
        val country: String,
        val obtained: Boolean,
        val repeated: Int
    )

    companion object {
        /** Columna _id de BaseColumns. */
        const val BaseColumnId = "_id"

        private const val SQL_CREATE_STICKERS =
            "CREATE TABLE ${StickerEntry.TABLE_NAME} (" +
                "$BaseColumnId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${StickerEntry.COLUMN_NUMBER} INTEGER NOT NULL UNIQUE," +
                "${StickerEntry.COLUMN_PLAYER_NAME} TEXT NOT NULL," +
                "${StickerEntry.COLUMN_COUNTRY} TEXT NOT NULL," +
                "${StickerEntry.COLUMN_OBTAINED} INTEGER NOT NULL DEFAULT 0," +
                "${StickerEntry.COLUMN_REPEATED_COUNT} INTEGER NOT NULL DEFAULT 0)"

        private const val SQL_CREATE_TRADES =
            "CREATE TABLE ${TradeEntry.TABLE_NAME} (" +
                "$BaseColumnId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${TradeEntry.COLUMN_STICKER_GIVEN} TEXT NOT NULL," +
                "${TradeEntry.COLUMN_STICKER_RECEIVED} TEXT NOT NULL," +
                "${TradeEntry.COLUMN_TRADE_DATE} TEXT NOT NULL)"
    }
}
