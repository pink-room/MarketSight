package dev.pinkroom.marketsight.data.local.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import dev.pinkroom.marketsight.common.fromJson

@ProvidedTypeConverter
class SymbolsConverter(val gson: Gson) {

    @TypeConverter
    fun toJson(symbols: List<String>?): String? = symbols?.let(gson::toJson)

    @TypeConverter
    fun fromJson(json: String?): List<String>? = json?.let(gson::fromJson)
}