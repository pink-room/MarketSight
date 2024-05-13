package dev.pinkroom.marketsight.domain.model.news

import dev.pinkroom.marketsight.common.Constants.ALL_SYMBOLS
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.common.popularSymbols
import dev.pinkroom.marketsight.domain.model.common.SubInfoSymbols
import java.time.LocalDate

data class NewsFilters(
    val symbols: List<SubInfoSymbols> = popularSymbols,
    val sort: List<SortType> = listOf(SortType.DESC, SortType.ASC),
    val sortBy: SortType = SortType.DESC,
    val startDateSort: LocalDate? = null,
    val endDateSort: LocalDate? = null,
){
    fun getSubscribedSymbols() = symbols
        .filter { it.isSubscribed && it.name != ALL_SYMBOLS }
        .map { it.symbol }
        .ifEmpty { null }
}
