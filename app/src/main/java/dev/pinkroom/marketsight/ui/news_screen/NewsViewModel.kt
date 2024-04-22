package dev.pinkroom.marketsight.ui.news_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.Constants.MAX_ITEMS_CAROUSEL
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.connection_network.ConnectivityObserver
import dev.pinkroom.marketsight.common.connection_network.ConnectivityObserver.Status.Available
import dev.pinkroom.marketsight.common.connection_network.ConnectivityObserver.Status.Unavailable
import dev.pinkroom.marketsight.common.paginator.DefaultPagination
import dev.pinkroom.marketsight.domain.model.common.PaginationInfo
import dev.pinkroom.marketsight.domain.model.news.NewsResponse
import dev.pinkroom.marketsight.domain.use_case.news.ChangeFilterNews
import dev.pinkroom.marketsight.domain.use_case.news.GetNews
import dev.pinkroom.marketsight.domain.use_case.news.GetRealTimeNews
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getRealTimeNews: GetRealTimeNews,
    private val getNews: GetNews,
    private val changeFilterNews: ChangeFilterNews,
    private val connectivityObserver: ConnectivityObserver,
    private val dispatchers: DispatcherProvider,
): ViewModel() {
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState = _uiState.asStateFlow()

    private val _action = Channel<NewsAction>()
    val action = _action.receiveAsFlow()

    private var paginationInfo = PaginationInfo()
    private val pagination = DefaultPagination<String, NewsResponse>(
        initialKey = null,
        onLoadUpdated = { isLoading ->
            paginationInfo = paginationInfo.copy(isLoading = isLoading)
            _uiState.update { it.copy(isLoadingMoreItems = isLoading) }
        },
        onRequest = { nextPage ->
            getNews(pageToken = nextPage)
        },
        getNextKey = {
            it.nextPageToken
        },
        onError = {
            _action.send(NewsAction.ShowSnackBar(message = R.string.get_news_error_message))
        },
        onSuccess = { data, newKey ->
            paginationInfo = paginationInfo.copy(
                endReached = newKey == null,
                page = newKey
            )
            _uiState.update { it.copy(news = it.news + data.news) }
        }
    )

    private var initNewsJob: Job? = null
    private var connectionStatus = Unavailable

    init {
        observeNetworkStatus()
        initNews()
        fetchRealTimeNews()
    }

    fun onEvent(event: NewsEvent){
        when(event){
            NewsEvent.RetryNews -> retryToGetNews()
            NewsEvent.RefreshNews -> refreshNews()
            NewsEvent.LoadMoreNews -> loadMoreNews()
        }
    }

    private fun observeNetworkStatus(){
        viewModelScope.launch(dispatchers.IO) {
            connectivityObserver.observe().distinctUntilChanged().collect{ statusNet ->
                connectionStatus = statusNet
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
                    val maxNumberNews = if (allNews.size >= MAX_ITEMS_CAROUSEL) MAX_ITEMS_CAROUSEL else allNews.size
                    val mainNews = allNews.take(maxNumberNews)
                    val remainingNews = if (maxNumberNews == allNews.size) mainNews
                    else allNews.drop(maxNumberNews)

                    pagination.reset(key = response.data.nextPageToken)
                    _uiState.update {
                        it.copy(
                            news = remainingNews, mainNews = mainNews,
                            isLoading = false, errorMessage = null, isRefreshing = false,
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false, isRefreshing = false,
                            errorMessage = R.string.get_news_error_message
                        )
                    }
                    if (uiState.value.news.isNotEmpty())
                        _action.send(
                            NewsAction.ShowSnackBar(message = R.string.get_news_error_message)
                        )
                }
            }
            initNewsJob = null
        }
    }

    private fun retryToGetNews() {
        if (initNewsJob == null && connectionStatus == Available) initNews()
    }

    private fun refreshNews() {
        if (initNewsJob == null){
            _uiState.update { it.copy(isRefreshing = true) }
            initNews()
        }
    }

    private fun loadMoreNews(){
        viewModelScope.launch(dispatchers.IO) {
            if (!paginationInfo.isLoading) pagination.loadNextItems()
        }
    }

    private fun fetchRealTimeNews() {
        viewModelScope.launch(dispatchers.IO) {
            getRealTimeNews().collect{ news ->
                _uiState.update { it.copy(realTimeNews = it.realTimeNews + news) }
            }
        }
    }
}