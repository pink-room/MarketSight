package dev.pinkroom.marketsight.ui.news_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.ConnectivityObserver
import dev.pinkroom.marketsight.common.ConnectivityObserver.Status.Available
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.domain.use_case.news.ChangeFilterNews
import dev.pinkroom.marketsight.domain.use_case.news.GetNews
import dev.pinkroom.marketsight.domain.use_case.news.GetRealTimeNews
import dev.pinkroom.marketsight.domain.use_case.news.SubscribeNews
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val subscribeNews: SubscribeNews,
    private val getRealTimeNews: GetRealTimeNews,
    private val getNews: GetNews,
    private val changeFilterNews: ChangeFilterNews,
    private val connectivityObserver: ConnectivityObserver,
    private val dispatchers: DispatcherProvider,
): ViewModel() {
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState = _uiState.asStateFlow()

    private var initNewsJob: Job? = null

    init {
        observeNetworkStatus()
        initNews()
    }

    fun onEvent(event: NewsEvent){
        when(event){
            NewsEvent.RetryNews -> retryToGetNews()
        }
    }

    private fun observeNetworkStatus(){
        viewModelScope.launch(dispatchers.IO) {
            connectivityObserver.observe().distinctUntilChanged().collect{ statusNet ->
                if (statusNet == Available && uiState.value.news.isEmpty() && initNewsJob == null)
                    initNews()
            }
        }
    }

    private fun initNews(){
        initNewsJob = viewModelScope.launch(dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            when(val response = getNews()){
                is Resource.Success -> {
                    val allNews = response.data.news
                    val maxNumberNews = if (allNews.size >= 5) 5 else allNews.size
                    val mainNews = allNews.subList(fromIndex = 0, toIndex = maxNumberNews)
                    _uiState.update {
                        it.copy(
                            news = response.data.news, mainNews = mainNews,
                            isLoading = false, errorMessage = null,
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = R.string.get_news_error_message)
                    }
                }
            }
            initNewsJob = null
        }
    }

    private fun retryToGetNews() {
        if (initNewsJob == null) initNews()
    }
}