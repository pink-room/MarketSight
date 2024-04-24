package dev.pinkroom.marketsight.ui.news_screen

import dev.pinkroom.marketsight.common.DateMomentType
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.domain.model.common.SubInfoSymbols

sealed class NewsEvent {
    data object RetryNews: NewsEvent()
    data object RefreshNews: NewsEvent()
    data object LoadMoreNews: NewsEvent()
    data class ShowOrHideFilters(val isToShow: Boolean? = null): NewsEvent()
    data class ChangeSort(val sort: SortType): NewsEvent()
    data class ChangeSymbol(val symbolToChange: SubInfoSymbols): NewsEvent()
    data class ChangeDate(val newDateInMillis: Long?, val dateMomentType: DateMomentType): NewsEvent()
}
