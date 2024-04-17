package dev.pinkroom.marketsight.ui.news_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pinkroom.marketsight.domain.use_case.news.ChangeFilterNews
import dev.pinkroom.marketsight.domain.use_case.news.GetNews
import dev.pinkroom.marketsight.domain.use_case.news.GetRealTimeNews
import dev.pinkroom.marketsight.domain.use_case.news.SubscribeNews
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val subscribeNews: SubscribeNews,
    private val getRealTimeNews: GetRealTimeNews,
    private val getNews: GetNews,
    private val changeFilterNews: ChangeFilterNews,
): ViewModel() {
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState = _uiState.asStateFlow()

    init {

    }
}