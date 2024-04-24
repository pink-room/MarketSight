package dev.pinkroom.marketsight.ui.news_screen

import androidx.annotation.DrawableRes
import dev.pinkroom.marketsight.common.DateMomentType
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.common.popularSymbols
import dev.pinkroom.marketsight.domain.model.common.SubInfoSymbols
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.ui.core.util.SelectableDatesImp
import java.time.LocalDate

data class NewsUiState(
    val isLoading: Boolean = true,
    val isLoadingMoreItems: Boolean = false,
    val isRefreshing: Boolean = false,
    val isToShowFilters: Boolean = false,
    @DrawableRes val errorMessage: Int? = null,
    val mainNews: List<NewsInfo> = listOf(),
    val news: List<NewsInfo> = listOf(),
    val realTimeNews: List<NewsInfo> = listOf(),
    val symbols: List<SubInfoSymbols> = popularSymbols,
    val sort: List<SortType> = listOf(SortType.DESC, SortType.ASC),
    val sortBy: SortType = SortType.DESC,
    val startDateSort: LocalDate? = null,
    val endDateSort: LocalDate? = null,
    val startSelectableDates: SelectableDatesImp = SelectableDatesImp(dateMomentType = DateMomentType.Start),
    val endSelectableDates: SelectableDatesImp = SelectableDatesImp(dateMomentType = DateMomentType.End)
)


