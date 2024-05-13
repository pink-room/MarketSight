package dev.pinkroom.marketsight.presentation.news_screen

import dev.pinkroom.marketsight.common.DateMomentType
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.domain.model.common.SubInfoSymbols

sealed class NewsEvent {
    data object RetryNews: NewsEvent()
    data object RetryRealTimeNewsSubscribe: NewsEvent()
    data object RefreshNews: NewsEvent()
    data object LoadMoreNews: NewsEvent()
    data object ClearAllFilters: NewsEvent()
    data object RevertFilters: NewsEvent()
    data object ApplyFilters: NewsEvent()
    data class ShowOrHideFilters(val isToShow: Boolean? = null): NewsEvent()
    data class ChangeSort(val sort: SortType): NewsEvent()
    data class ChangeSymbol(val symbolToChange: SubInfoSymbols): NewsEvent()
    data class ChangeDate(val newDateInMillis: Long?, val dateMomentType: DateMomentType): NewsEvent()
}
