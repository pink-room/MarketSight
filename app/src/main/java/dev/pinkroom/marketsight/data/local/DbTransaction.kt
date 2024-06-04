package dev.pinkroom.marketsight.data.local

import androidx.room.withTransaction
import javax.inject.Inject

class DbTransaction @Inject constructor(
    private val db: MarketSightDatabase,
) {
    suspend operator fun <T> invoke(block: suspend () -> T) = db.withTransaction(block)
}