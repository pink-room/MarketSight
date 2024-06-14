package dev.pinkroom.marketsight.presentation.news_screen

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.Constants.ALL_SYMBOLS
import dev.pinkroom.marketsight.common.Constants.LIMIT_NEWS
import dev.pinkroom.marketsight.common.Constants.MAX_ITEMS_CAROUSEL
import dev.pinkroom.marketsight.common.DateMomentType
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.common.atEndOfTheDay
import dev.pinkroom.marketsight.common.connection_network.ConnectivityObserver
import dev.pinkroom.marketsight.common.connection_network.ConnectivityObserver.Status.Available
import dev.pinkroom.marketsight.common.connection_network.ConnectivityObserver.Status.Unavailable
import dev.pinkroom.marketsight.common.paginator.DefaultPagination
import dev.pinkroom.marketsight.domain.model.common.PaginationInfo
import dev.pinkroom.marketsight.domain.model.common.SubInfoSymbols
import dev.pinkroom.marketsight.domain.model.news.NewsFilters
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.domain.model.news.NewsResponse
import dev.pinkroom.marketsight.domain.use_case.news.ChangeFilterRealTimeNews
import dev.pinkroom.marketsight.domain.use_case.news.GetNews
import dev.pinkroom.marketsight.domain.use_case.news.GetRealTimeNews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getRealTimeNews: GetRealTimeNews,
    private val getNews: GetNews,
    private val changeFilterRealTimeNews: ChangeFilterRealTimeNews,
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
        onRequest = { nextPageToken, nextPageNumber ->
            val filters = uiState.value.filters
            getNews(
                pageToken = nextPageToken,
                fetchFromRemote = paginationInfo.fetchFromRemote,
                offset = nextPageNumber * LIMIT_NEWS,
                limitPerPage = LIMIT_NEWS,
                sortType = filters.sortBy,
                symbols = filters.getSubscribedSymbols(),
                startDate = filters.startDateSort?.atStartOfDay(),
                endDate = filters.endDateSort?.atEndOfTheDay(),
            )
        },
        getNextKey = {
            it.nextPageToken
        },
        onError = {
            if (paginationInfo.fetchFromRemote)
                _action.send(NewsAction.ShowSnackBar(message = R.string.get_news_error_message, duration = SnackbarDuration.Long))
        },
        onSuccess = { data, newKey ->
            paginationInfo = paginationInfo.copy(
                endReached = newKey == null,
                pageToken = newKey,
            )
            _uiState.update { it.copy(news = it.news + data.news) }
        }
    )

    private var initNewsJob: Job? = null
    private var connectionStatus = Unavailable
    private var previousFilters: NewsFilters = uiState.value.filters

    init {
        initNews(clearCache = true)
        fetchRealTimeNews()
        observeNetworkStatus()
    }

    fun onEvent(event: NewsEvent) {
        when(event) {
            NewsEvent.RetryNews -> retryToGetNews()
            NewsEvent.RetryRealTimeNewsSubscribe -> updateFiltersRealTimeNews(newFilters = previousFilters)
            NewsEvent.RefreshNews -> refreshNews()
            NewsEvent.LoadMoreNews -> loadMoreNews()
            NewsEvent.ApplyFilters -> applyFilters()
            NewsEvent.ClearAllFilters -> clearAllFilters()
            NewsEvent.RevertFilters -> revertFilters()
            is NewsEvent.ShowOrHideFilters -> showOrHideFilters(isToShow = event.isToShow)
            is NewsEvent.ChangeSort -> changeSort(newSort = event.sort)
            is NewsEvent.ChangeSymbol -> changeSymbols(symbolToChange = event.symbolToChange)
            is NewsEvent.ChangeDate -> changeDate(dateInMillis = event.newDateInMillis, dateMomentType = event.dateMomentType)
        }
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch(dispatchers.IO) {
            connectivityObserver.observe().distinctUntilChanged().collect{ statusNet ->
                connectionStatus = statusNet
                if (statusNet == Available && (uiState.value.news.isEmpty() || !paginationInfo.fetchFromRemote) && initNewsJob == null) {
                    _action.trySend(NewsAction.CloseSnackBar)
                    initNews(clearCache = true, tryToSubscribeFilter = true)
                }
            }
        }
    }

    private fun initNews(
        clearCache: Boolean = false,
        tryToSubscribeFilter: Boolean = false
    ) {
        initNewsJob = viewModelScope.launch(dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            paginationInfo = paginationInfo.copy(fetchFromRemote = true)

            val filters = uiState.value.filters
            val response = getNews(
                sortType = filters.sortBy,
                limitPerPage = LIMIT_NEWS,
                symbols = filters.getSubscribedSymbols(),
                startDate = filters.startDateSort?.atStartOfDay(),
                endDate = filters.endDateSort?.atEndOfTheDay(),
                cleanCache = clearCache,
                fetchFromRemote = paginationInfo.fetchFromRemote
            )
            when(response){
                is Resource.Success -> {
                    pagination.reset(key = response.data.nextPageToken)
                    paginationInfo = paginationInfo.copy(endReached = response.data.nextPageToken == null)
                    initNewsState(allNews = response.data.news)
                    if (tryToSubscribeFilter) {
                        updateFiltersRealTimeNews(uiState.value.filters)
                    }
                }
                is Resource.Error -> {
                    initNewsState(allNews = response.data?.news ?: emptyList(), errorMessage = R.string.get_news_error_message)
                    response.data?.let {
                        pagination.reset(key = response.data.nextPageToken)
                        if (it.news.isNotEmpty()) paginationInfo = paginationInfo.copy(endReached = it.nextPageToken == null, fetchFromRemote = false)
                    }
                    if (uiState.value.news.isNotEmpty())
                        _action.send(
                            NewsAction.ShowSnackBar(
                                message = R.string.get_news_error_message, actionMessage = R.string.retry,
                                duration = SnackbarDuration.Indefinite, retryEvent = NewsEvent.RefreshNews
                            )
                        )
                }
            }
            initNewsJob = null
        }
    }

    private fun initNewsState(allNews: List<NewsInfo>, errorMessage: Int? = null) {
        val maxNumberNews = if (allNews.size >= MAX_ITEMS_CAROUSEL) MAX_ITEMS_CAROUSEL else allNews.size
        val mainNews = allNews.take(maxNumberNews)
        val remainingNews = if (maxNumberNews == allNews.size) mainNews
        else allNews.drop(maxNumberNews)

        _uiState.update {
            it.copy(
                news = remainingNews, mainNews = mainNews,
                isLoading = false, errorMessage = errorMessage, isRefreshing = false,
            )
        }
    }

    private fun retryToGetNews() {
        if (initNewsJob == null && connectionStatus == Available) initNews()
    }

    private fun refreshNews() {
        if (initNewsJob == null){
            _action.trySend(NewsAction.CloseSnackBar)
            _uiState.update { it.copy(isRefreshing = true) }
            initNews(clearCache = true, tryToSubscribeFilter = true)
        }
    }

    private fun loadMoreNews() {
        viewModelScope.launch(dispatchers.IO) {
            if (!paginationInfo.isLoading && !paginationInfo.endReached) pagination.loadNextItems()
        }
    }

    private fun fetchRealTimeNews() {
        viewModelScope.launch(dispatchers.IO) {
            getRealTimeNews().collect{ news ->
                _uiState.update { it.copy(realTimeNews = it.realTimeNews + news) }
            }
        }
    }

    private fun showOrHideFilters(isToShow: Boolean? = null) {
        if (uiState.value.isLoading) return
        _uiState.update { it.copy(isToShowFilters = isToShow ?: !it.isToShowFilters) }
    }

    private fun changeSort(newSort: SortType) {
        if (uiState.value.filters.sortBy == newSort) return
        _uiState.update { it.copy(filters = it.filters.copy(sortBy = newSort)) }
    }

    private fun changeSymbols(symbolToChange: SubInfoSymbols) {
        val symbolsSubscribed = uiState.value.filters.symbols.filter { it.isSubscribed }
        val totalSizeSubscribed = if (symbolsSubscribed.contains(symbolToChange))
            symbolsSubscribed.size - 1
        else symbolsSubscribed.size

        val needToSubscribeAll = totalSizeSubscribed == 0 || symbolToChange.name == ALL_SYMBOLS

        val newListSymbols = uiState.value.filters.symbols.map { item ->
            when {
                item.name == ALL_SYMBOLS -> item.copy(isSubscribed = needToSubscribeAll)
                item.symbol == symbolToChange.symbol -> item.copy(isSubscribed = !item.isSubscribed)
                needToSubscribeAll -> item.copy(isSubscribed = false)
                else -> item.copy()
            }
        }
        _uiState.update { it.copy(filters = it.filters.copy(symbols = newListSymbols)) }
    }

    private fun changeDate(dateInMillis: Long?, dateMomentType: DateMomentType) {
        val date = dateInMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
        when(dateMomentType){
            DateMomentType.End -> {
                _uiState.update { it.copy(filters = it.filters.copy(endDateSort = date)) }
            }
            DateMomentType.Start -> {
                _uiState.update { it.copy(filters = it.filters.copy(startDateSort = date)) }
            }
        }
    }

    private fun applyFilters() {
        _uiState.update { it.copy(isToShowFilters = false) }
        if (previousFilters != uiState.value.filters)
            updateNewsWithNewFilters(newFilters = uiState.value.filters)
    }

    private fun clearAllFilters() {
        val baseFilters = NewsFilters()
        if (previousFilters != baseFilters) updateNewsWithNewFilters(newFilters = baseFilters)
        _uiState.update { it.copy(filters = baseFilters, isToShowFilters = false) }
    }

    private fun revertFilters() = _uiState.update { it.copy(filters = previousFilters, isToShowFilters = false) }

    private fun updateNewsWithNewFilters(newFilters: NewsFilters) {
        initNews()
        if (previousFilters.symbols != newFilters.symbols) updateFiltersRealTimeNews(newFilters)
        previousFilters = newFilters
    }

    private fun updateFiltersRealTimeNews(newFilters: NewsFilters) {
        viewModelScope.launch(dispatchers.IO) {
            _uiState.update { it.copy(realTimeNews = emptyList()) }
            changeFilterRealTimeNews(
                subscribeSymbols = newFilters.symbols.filter { it.isSubscribed }.map { it.symbol },
                unsubscribeSymbols = newFilters.symbols.filter { !it.isSubscribed }.map { it.symbol }
            ).collect{ response ->
                when(response){
                    is Resource.Success -> Unit
                    is Resource.Error -> {
                        _action.send(
                            NewsAction.ShowSnackBar(
                                message = R.string.error_subscription_real_time_news,
                                duration = SnackbarDuration.Indefinite,
                                actionMessage = R.string.retry,
                                retryEvent = NewsEvent.RetryRealTimeNewsSubscribe,
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Stop Receiving Live News When user is not in the screen
        CoroutineScope(dispatchers.IO).launch {
            changeFilterRealTimeNews(
                subscribeSymbols = null,
                unsubscribeSymbols = uiState.value.filters.getSubscribedSymbols()
            ).drop(1).collect {}
        }
    }
}