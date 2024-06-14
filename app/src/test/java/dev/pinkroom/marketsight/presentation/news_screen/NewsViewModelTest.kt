package dev.pinkroom.marketsight.presentation.news_screen

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isGreaterThan
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import dev.pinkroom.marketsight.common.Constants.ALL_SYMBOLS
import dev.pinkroom.marketsight.common.Constants.LIMIT_NEWS
import dev.pinkroom.marketsight.common.Constants.MAX_ITEMS_CAROUSEL
import dev.pinkroom.marketsight.common.DateMomentType
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.common.atEndOfTheDay
import dev.pinkroom.marketsight.common.connection_network.ConnectivityObserver
import dev.pinkroom.marketsight.data.mapper.toNewsInfo
import dev.pinkroom.marketsight.domain.model.news.NewsFilters
import dev.pinkroom.marketsight.domain.model.news.NewsInfo
import dev.pinkroom.marketsight.domain.model.news.NewsResponse
import dev.pinkroom.marketsight.domain.use_case.news.ChangeFilterRealTimeNewsUseCase
import dev.pinkroom.marketsight.domain.use_case.news.GetNewsUseCase
import dev.pinkroom.marketsight.domain.use_case.news.GetRealTimeNewsUseCase
import dev.pinkroom.marketsight.factories.NewsDtoFactory
import dev.pinkroom.marketsight.util.MainCoroutineRule
import dev.pinkroom.marketsight.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset

@ExperimentalCoroutinesApi
class NewsViewModelTest{

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val dispatchers = TestDispatcherProvider()
    private val newsFactory = NewsDtoFactory()
    private val getNewsUseCase = mockk<GetNewsUseCase>(relaxed = true, relaxUnitFun = true)
    private val getRealTimeNewsUseCase = mockk<GetRealTimeNewsUseCase>(relaxed = true, relaxUnitFun = true)
    private val changeFilterRealTimeNewsUseCase = mockk<ChangeFilterRealTimeNewsUseCase>(relaxed = true, relaxUnitFun = true)
    private val connectivityObserver = mockk<ConnectivityObserver>(relaxed = true, relaxUnitFun = true)
    private lateinit var newsViewModel: NewsViewModel

    private fun initViewModel(mockGetNews: Boolean = false, mockNetwork: Boolean = false) {
        if (mockGetNews){
            val news = NewsDtoFactory().buildList(number = LIMIT_NEWS).map { it.toNewsInfo() }
            mockResponseGetNewsSuccess(news)
        }

        if (mockNetwork) {
            coEvery {
                connectivityObserver.observe()
            }.returns(
                flow {
                    emit(ConnectivityObserver.Status.Available)
                }
            )
        }

        newsViewModel = NewsViewModel(
            getNewsUseCase = getNewsUseCase,
            getRealTimeNewsUseCase = getRealTimeNewsUseCase,
            changeFilterRealTimeNewsUseCase = changeFilterRealTimeNewsUseCase,
            connectivityObserver = connectivityObserver,
            dispatchers = dispatchers,
        )
    }

    @Test
    fun `Given access to Net, When init VM, Then Success on Get News and RealTime News`() = runTest {
        // GIVEN
        val news = newsFactory.buildList(number = LIMIT_NEWS).map { it.toNewsInfo() }
        mockResponseGetNewsSuccess(news)
        mockGetRealTimeNews(news)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = newsViewModel.uiState.value
        val filters = uiState.filters

        coVerify {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = filters.sortBy,
                symbols = filters.getSubscribedSymbols(),
                startDate = filters.startDateSort?.atStartOfDay(),
                endDate = filters.endDateSort?.atEndOfTheDay(),
                offset = 0,
                fetchFromRemote = true,
                cleanCache = true,
            )
        }
        assertThat(uiState.news).isNotEmpty()
        assertThat(uiState.mainNews).isNotEmpty()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.mainNews).isEqualTo(news.take(MAX_ITEMS_CAROUSEL))
        assertThat(uiState.news).isEqualTo(news.drop(MAX_ITEMS_CAROUSEL))

        coVerify { getRealTimeNewsUseCase.invoke() }
        assertThat(uiState.realTimeNews).isNotEmpty()
    }

    @Test
    fun `Start Without Net, When init VM, Then Error on Get News`() = runTest {
        // GIVEN
        mockResponseGetNewsError()

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = newsViewModel.uiState.value

        coVerify {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = any(), fetchFromRemote = any(), cleanCache = any(),
            )
        }
        assertThat(uiState.news).isEmpty()
        assertThat(uiState.mainNews).isEmpty()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.errorMessage).isNotNull()
    }

    @Test
    fun `Start Without Net, When init VM, Then Error on Get News and show cached news`() = runTest {
        // GIVEN
        val news = newsFactory.buildList(number = LIMIT_NEWS).map { it.toNewsInfo() }
        val data = NewsResponse(news = news, nextPageToken = "ASSAAS")
        mockResponseGetNewsError(data = data)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = newsViewModel.uiState.value

        coVerify {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = any(), fetchFromRemote = any(), cleanCache = any(),
            )
        }
        assertThat(uiState.news).isNotEmpty()
        assertThat(uiState.mainNews).isNotEmpty()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.mainNews).isEqualTo(news.take(MAX_ITEMS_CAROUSEL))
        assertThat(uiState.news).isEqualTo(news.drop(MAX_ITEMS_CAROUSEL))
        assertThat(uiState.errorMessage).isNotNull()

        val action = newsViewModel.action.first()
        assertThat(action).isInstanceOf(NewsAction.ShowSnackBar::class.java)
    }

    @Test
    fun `Given without Net, When init VM, Then it gets net and automatically calls GetNews`() = runTest {
        // GIVEN
        val news = NewsDtoFactory().buildList(number = LIMIT_NEWS).map { it.toNewsInfo() }
        mockNetwork()
        mockResponseGetNewsErrorThenSuccess(news)

        // WHEN
        initViewModel()

        // THEN
        newsViewModel.uiState.test {
            var item = awaitItem()
            assertThat(item.isLoading).isTrue()
            item = awaitItem()
            assertThat(item.isLoading).isFalse()
            assertThat(item.news).isEmpty()
            assertThat(item.errorMessage).isNotNull()
        }
        newsViewModel.action.test {
            assertThat(awaitItem()).isInstanceOf(NewsAction.CloseSnackBar::class.java)
        }
        advanceUntilIdle()
        val uiState = newsViewModel.uiState.value

        coVerify(exactly = 2) {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = any(), fetchFromRemote = any(), cleanCache = any(),
            )
        }
        assertThat(uiState.news).isNotEmpty()
        assertThat(uiState.mainNews).isNotEmpty()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.mainNews).isEqualTo(news.take(MAX_ITEMS_CAROUSEL))
        assertThat(uiState.news).isEqualTo(news.drop(MAX_ITEMS_CAROUSEL))
    }

    @Test
    fun `When RetryNews event, then init news`() = runTest {
        // GIVEN
        initViewModel()
        val news = NewsDtoFactory().buildList(number = LIMIT_NEWS).map { it.toNewsInfo() }
        mockResponseGetNewsSuccess(news)

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.RetryNews)

        // THEN
        advanceUntilIdle()
        val uiState = newsViewModel.uiState.value

        coVerify {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                cleanCache = true, fetchFromRemote = true, offset = 0,
            )
        }
        assertThat(uiState.news).isNotEmpty()
        assertThat(uiState.mainNews).isNotEmpty()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.mainNews).isEqualTo(news.take(MAX_ITEMS_CAROUSEL))
        assertThat(uiState.news).isEqualTo(news.drop(MAX_ITEMS_CAROUSEL))
    }

    @Test
    fun `When request for news is running, Then if the call is made again it is ignored`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.RetryNews)
        newsViewModel.onEvent(event = NewsEvent.RetryNews)

        // THEN
        advanceUntilIdle()

        coVerify(exactly = 1) {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                cleanCache = any(), fetchFromRemote = any(), offset = any(),
            )
        }
    }

    @Test
    fun `When RetryRealTimeNewsSubscribe event is called, then send request to service`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true, mockNetwork = true)
        val filters = newsViewModel.uiState.value.filters

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.RetryRealTimeNewsSubscribe)

        // THEN
        advanceUntilIdle()

        coVerify {
            changeFilterRealTimeNewsUseCase.invoke(
                subscribeSymbols = filters.symbols.filter { it.isSubscribed }.map { it.symbol },
                unsubscribeSymbols = filters.symbols.filter { !it.isSubscribed }.map { it.symbol },
            )
        }
    }

    @Test
    fun `When RetryRealTimeNewsSubscribe event is called, then receive Error from service and send Action to show SnackBar`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true, mockNetwork = true)
        val filters = newsViewModel.uiState.value.filters
        mockChangeFilterRealTimeNewsError()

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.RetryRealTimeNewsSubscribe)

        // THEN
        advanceUntilIdle()

        coVerify {
            changeFilterRealTimeNewsUseCase.invoke(
                subscribeSymbols = filters.symbols.filter { it.isSubscribed }.map { it.symbol },
                unsubscribeSymbols = filters.symbols.filter { !it.isSubscribed }.map { it.symbol },
            )
        }
        val action = newsViewModel.action.first()
        assertThat(action).isInstanceOf(NewsAction.ShowSnackBar::class.java)
    }

    @Test
    fun `When RefreshNews event is called, then call init news`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)
        advanceUntilIdle()

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.RefreshNews)

        // THEN
        newsViewModel.uiState.test {
            val uiState = awaitItem()
            assertThat(uiState.isRefreshing).isTrue()
        }

        coVerify {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = 0, fetchFromRemote = true, cleanCache = true,
            )
        }
    }

    @Test
    fun `When LoadMoreNews event is called, then get news`() = runTest {
        // GIVEN
        val news = NewsDtoFactory().buildList(number = LIMIT_NEWS).map { it.toNewsInfo() }
        val nextToken = "RandomStringToken"
        mockResponseGetNewsSuccess(news = news, nextPageToken = nextToken)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.LoadMoreNews)
        advanceUntilIdle()

        // THEN
        val uiState = newsViewModel.uiState.value

        coVerify {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = 0, cleanCache = true, fetchFromRemote = true
            )
        }
        coVerify {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = LIMIT_NEWS, cleanCache = false, fetchFromRemote = true
            )
        }
        assertThat(uiState.news.size).isGreaterThan(news.size)
    }

    @Test
    fun `When LoadMoreNews event is called to fetch from remote, then get news error`() = runTest {
        // GIVEN
        val news = NewsDtoFactory().buildList(number = LIMIT_NEWS).map { it.toNewsInfo() }
        val nextToken = "RandomStringToken"
        mockResponseGetNewsLoadMoreError(news = news, nextPageToken = nextToken)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.LoadMoreNews)
        advanceUntilIdle()

        // THEN
        val action = newsViewModel.action.first()

        coVerify {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = 0, cleanCache = true, fetchFromRemote = true
            )
        }
        coVerify {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = LIMIT_NEWS, cleanCache = false, fetchFromRemote = true
            )
        }
        assertThat(action is NewsAction.ShowSnackBar).isTrue()
    }

    @Test
    fun `When LoadMoreNews event is called after read cached news, then get news`() = runTest {
        // GIVEN
        val news = NewsDtoFactory().buildList(number = LIMIT_NEWS).map { it.toNewsInfo() }
        val nextToken = "RandomStringToken"
        mockResponseGetCachedNewsSuccess(news = news, nextPageToken = nextToken)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.LoadMoreNews)
        advanceUntilIdle()

        // THEN
        val uiState = newsViewModel.uiState.value

        coVerify {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = 0, cleanCache = true, fetchFromRemote = true
            )
        }
        coVerify {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = LIMIT_NEWS, cleanCache = false, fetchFromRemote = false
            )
        }
        assertThat(uiState.news.size).isGreaterThan(news.size)
    }

    @Test
    fun `When LoadMoreNews event is called twice, then just one call to get news`() = runTest {
        // GIVEN
        val news = NewsDtoFactory().buildList(number = LIMIT_NEWS).map { it.toNewsInfo() }
        val nextToken = "RandomStringToken"
        mockResponseGetNewsSuccess(news = news, nextPageToken = nextToken)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.LoadMoreNews)
        newsViewModel.onEvent(event = NewsEvent.LoadMoreNews)
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 2) { // IS 2 BECAUSE ON INIT VIEWMODEL IS CALLED GET NEWS
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                fetchFromRemote = any(), cleanCache = any(), offset = any(),
            )
        }
    }

    @Test
    fun `When LoadMoreNews event is called but end is reached, then just ignore`() = runTest {
        // GIVEN
        val news = NewsDtoFactory().buildList(number = LIMIT_NEWS).map { it.toNewsInfo() }
        mockResponseGetNewsSuccess(news = news, nextPageToken = null)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.LoadMoreNews)

        // THEN
        coVerify(exactly = 1) { // IS 1 BECAUSE ON INIT VIEWMODEL IS CALLED GET NEWS
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = any(), cleanCache = any(), fetchFromRemote = any(),
            )
        }
    }

    @Test
    fun `When ShowOrHideFilters event is called, Then change isToShowFilters`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)
        advanceUntilIdle()
        val isToShow = true

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.ShowOrHideFilters(isToShow = isToShow))

        // THEN
        val uiState = newsViewModel.uiState.value
        assertThat(uiState.isToShowFilters).isEqualTo(isToShow)
    }

    @Test
    fun `When ShowOrHideFilters event is called but is loading, Then show filters is false`() = runTest {
        // GIVEN
        val news = NewsDtoFactory().buildList(number = LIMIT_NEWS).map { it.toNewsInfo() }
        mockResponseGetNewsSuccess(news = news, nextPageToken = null)
        val isToShow = true

        // WHEN
        initViewModel(mockGetNews = true)
        newsViewModel.onEvent(event = NewsEvent.ShowOrHideFilters(isToShow = isToShow))
        advanceUntilIdle()

        // THEN
        val uiState = newsViewModel.uiState.value
        assertThat(uiState.isToShowFilters).isEqualTo(!isToShow)
    }

    @Test
    fun `When ChangeSort event is called, Then update filters in uiState`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)
        val newSort = SortType.ASC

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.ChangeSort(sort = newSort))
        advanceUntilIdle()

        // THEN
        val filters = newsViewModel.uiState.value.filters
        assertThat(filters.sortBy).isEqualTo(newSort)
    }

    @Test
    fun `When ChangeSymbol event is called, Then update filters in uiState`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)
        val allSymbols = newsViewModel.uiState.value.filters.symbols
        val newSymbolToChange = allSymbols.last()

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.ChangeSymbol(symbolToChange = newSymbolToChange))
        advanceUntilIdle()

        // THEN
        val symbols = newsViewModel.uiState.value.filters.symbols
        val lastSymbol = symbols.last()
        val all = symbols.find { it.name == ALL_SYMBOLS }
        assertThat(lastSymbol.isSubscribed).isNotEqualTo(newSymbolToChange.isSubscribed)
        assertThat(all!!.isSubscribed).isFalse()
    }

    @Test
    fun `When ChangeSymbol event is called to unsubscribe last subscribed symbol, Then all symbol is subscribed`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)
        newsViewModel.onEvent(event = NewsEvent.ChangeSymbol(symbolToChange = newsViewModel.uiState.value.filters.symbols.last())) // SUBSCRIBE LAST
        advanceUntilIdle()

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.ChangeSymbol(symbolToChange = newsViewModel.uiState.value.filters.symbols.last())) // UNSUBSCRIBE LAST
        advanceUntilIdle()

        // THEN
        val symbols = newsViewModel.uiState.value.filters.symbols
        val lastSymbol = symbols.last()
        val all = symbols.find { it.name == ALL_SYMBOLS }
        assertThat(lastSymbol.isSubscribed).isFalse()
        assertThat(all!!.isSubscribed).isTrue()
    }

    @Test
    fun `When ChangeSymbol event is called to subscribe ALL, Then all symbol is subscribed and other items are unsubscribed`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)
        val symbolToChange = newsViewModel.uiState.value.filters.symbols.size - 1
        newsViewModel.onEvent(event = NewsEvent.ChangeSymbol(symbolToChange = newsViewModel.uiState.value.filters.symbols.elementAt(symbolToChange)))
        newsViewModel.onEvent(event = NewsEvent.ChangeSymbol(symbolToChange = newsViewModel.uiState.value.filters.symbols.last()))
        advanceUntilIdle()

        // WHEN
        var symbols = newsViewModel.uiState.value.filters.symbols
        var all = symbols.find { it.name == ALL_SYMBOLS }
        newsViewModel.onEvent(event = NewsEvent.ChangeSymbol(symbolToChange = all!!)) // SUBSCRIBE ALL
        advanceUntilIdle()

        // THEN
        symbols = newsViewModel.uiState.value.filters.symbols
        all = symbols.find { it.name == ALL_SYMBOLS }
        assertThat(all!!.isSubscribed).isTrue()
        assertThat(symbols.count { it.isSubscribed }).isEqualTo(1)
    }

    @Test
    fun `When ChangeDate event is called, Then update date`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)
        val dateInMillis = 1714399712434L

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.ChangeDate(newDateInMillis = dateInMillis, dateMomentType = DateMomentType.Start))
        newsViewModel.onEvent(event = NewsEvent.ChangeDate(newDateInMillis = dateInMillis, dateMomentType = DateMomentType.End))
        advanceUntilIdle()

        // THEN
        val expectedDate = Instant.ofEpochMilli(dateInMillis).atZone(ZoneOffset.UTC).toLocalDate()
        val filters = newsViewModel.uiState.value.filters
        assertThat(filters.startDateSort).isEqualTo(expectedDate)
        assertThat(filters.endDateSort).isEqualTo(expectedDate)
    }

    @Test
    fun `When ApplyFilters event is called, Then update news with new filters`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true, mockNetwork = true)
        val dateInMillis = 1714399712434L

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.ChangeDate(newDateInMillis = dateInMillis, dateMomentType = DateMomentType.Start))
        newsViewModel.onEvent(event = NewsEvent.ChangeSymbol(symbolToChange = newsViewModel.uiState.value.filters.symbols.last()))
        advanceUntilIdle()
        newsViewModel.onEvent(event = NewsEvent.ApplyFilters)
        advanceUntilIdle()

        // THEN
        val expectedDate = Instant.ofEpochMilli(dateInMillis).atZone(ZoneOffset.UTC).toLocalDate()
        coVerify(exactly = 1) {
            getNewsUseCase.invoke(
                startDate = expectedDate.atStartOfDay(),
                symbols = listOf(newsViewModel.uiState.value.filters.symbols.last().symbol),
                pageToken = any(), endDate = any(), sortType = any(),
                limitPerPage = any(), fetchFromRemote = any(), cleanCache = true, offset = any(),
            )
        }
        coVerify(exactly = 1) {
            getNewsUseCase.invoke(
                startDate = expectedDate.atStartOfDay(),
                symbols = listOf(newsViewModel.uiState.value.filters.symbols.last().symbol),
                pageToken = any(), endDate = any(), sortType = any(),
                limitPerPage = any(), fetchFromRemote = any(), cleanCache = false, offset = any(),
            )
        }
        verify {
            changeFilterRealTimeNewsUseCase.invoke(
                subscribeSymbols = any(),
                unsubscribeSymbols = any(),
            )
        }
    }

    @Test
    fun `When ApplyFilters event is called and filters are the same, Then just ignore`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.ApplyFilters)
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 1) { // 1 is because GetNews is called in INIT
            getNewsUseCase.invoke(
                startDate = any(), symbols = any(), pageToken = any(), endDate = any(), sortType = any(),
                limitPerPage = any(), fetchFromRemote = any(), cleanCache = any(), offset = any(),
            )
        }
        verify(exactly = 0) {
            changeFilterRealTimeNewsUseCase.invoke()
        }
    }

    @Test
    fun `When ApplyFilters event is called and filters don't change in symbols, Then real time changeFilterRealTimeNews is not called`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)
        val dateInMillis = 1714399712434L

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.ChangeDate(newDateInMillis = dateInMillis, dateMomentType = DateMomentType.Start))
        newsViewModel.onEvent(event = NewsEvent.ApplyFilters)
        advanceUntilIdle()

        // THEN
        coVerify(exactly = 1) {
            getNewsUseCase.invoke(
                startDate = any(), symbols = any(),
                pageToken = any(), endDate = any(), sortType = any(),
                limitPerPage = any(), fetchFromRemote = any(), cleanCache = true, offset = any(),
            )
        }
        coVerify(exactly = 1) {
            getNewsUseCase.invoke(
                startDate = any(), symbols = any(),
                pageToken = any(), endDate = any(), sortType = any(),
                limitPerPage = any(), fetchFromRemote = any(), cleanCache = false, offset = any(),
            )
        }
        verify(exactly = 0) {
            changeFilterRealTimeNewsUseCase.invoke()
        }
    }

    @Test
    fun `When ClearAllFilters event is called, Then update news related to base filters`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)
        val dateInMillis = 1714399712434L

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.ChangeDate(newDateInMillis = dateInMillis, dateMomentType = DateMomentType.Start))
        newsViewModel.onEvent(event = NewsEvent.ApplyFilters)
        newsViewModel.onEvent(event = NewsEvent.ClearAllFilters)
        advanceUntilIdle()

        // THEN
        val uiState = newsViewModel.uiState.value
        assertThat(uiState.isToShowFilters).isFalse()
        assertThat(uiState.filters).isEqualTo(NewsFilters())
        coVerify(exactly = 2) {
            getNewsUseCase.invoke(
                startDate = any(), symbols = any(),
                pageToken = any(), endDate = any(), sortType = any(),
                limitPerPage = any(), fetchFromRemote = any(), cleanCache = false, offset = any(),
            )
        }
        coVerify(exactly = 1) {
            getNewsUseCase.invoke(
                startDate = any(), symbols = any(),
                pageToken = any(), endDate = any(), sortType = any(),
                limitPerPage = any(), fetchFromRemote = any(), cleanCache = true, offset = any(),
            )
        }
    }

    @Test
    fun `When ClearAllFilters event is called but the filters applied are the base filters, Then just close the bottom sheet`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)
        val dateInMillis = 1714399712434L

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.ChangeDate(newDateInMillis = dateInMillis, dateMomentType = DateMomentType.Start))
        newsViewModel.onEvent(event = NewsEvent.ClearAllFilters)
        advanceUntilIdle()

        // THEN
        val uiState = newsViewModel.uiState.value
        assertThat(uiState.isToShowFilters).isFalse()
        assertThat(uiState.filters).isEqualTo(NewsFilters())
        coVerify(exactly = 1) {
            getNewsUseCase.invoke(
                startDate = any(), symbols = any(),
                pageToken = any(), endDate = any(), sortType = any(),
                limitPerPage = any(), fetchFromRemote = any(), cleanCache = any(), offset = any(),
            )
        }
    }

    @Test
    fun `When RevertFilters event is called, Then just revert filters to last filters`() = runTest {
        // GIVEN
        initViewModel(mockGetNews = true)
        val dateInMillis = 1714399712434L

        // WHEN
        newsViewModel.onEvent(event = NewsEvent.ChangeDate(newDateInMillis = dateInMillis, dateMomentType = DateMomentType.Start))
        newsViewModel.onEvent(event = NewsEvent.ApplyFilters)
        advanceUntilIdle()
        val lastFilters = newsViewModel.uiState.value.filters

        newsViewModel.onEvent(event = NewsEvent.ChangeDate(newDateInMillis = null, dateMomentType = DateMomentType.Start))
        newsViewModel.onEvent(event = NewsEvent.RevertFilters)
        advanceUntilIdle()

        // THEN
        val uiState = newsViewModel.uiState.value
        assertThat(uiState.isToShowFilters).isFalse()
        assertThat(uiState.filters).isEqualTo(lastFilters)
    }

    private fun mockChangeFilterRealTimeNewsError() {
        coEvery {
            changeFilterRealTimeNewsUseCase.invoke(
                subscribeSymbols = any(),
                unsubscribeSymbols = any(),
            )
        }.returns(
            flow {
                emit(Resource.Error(message = "Error"))
            }
        )
    }

    private fun mockResponseGetNewsSuccess(
        news: List<NewsInfo>,
        nextPageToken: String? = null,
    ) {
        coEvery {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                cleanCache = any(), fetchFromRemote = any(), offset = any(),
            )
        }.coAnswers {
            delay(1000)
            Resource.Success(data = NewsResponse(news = news, nextPageToken = nextPageToken))
        }
    }

    private fun mockResponseGetNewsLoadMoreError(
        news: List<NewsInfo>,
        nextPageToken: String? = null,
    ) {
        coEvery {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                cleanCache = any(), fetchFromRemote = any(), offset = any(),
            )
        }.returnsMany(
            Resource.Success(data = NewsResponse(news = news, nextPageToken = nextPageToken)),
            Resource.Error(message = "Error on Load More")
        )
    }

    private fun mockResponseGetCachedNewsSuccess(
        news: List<NewsInfo>,
        nextPageToken: String? = null,
    ) {
        coEvery {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                cleanCache = any(), fetchFromRemote = any(), offset = any(),
            )
        }.returnsMany(
            Resource.Error(data = NewsResponse(news = news, nextPageToken = nextPageToken)),
            Resource.Success(data = NewsResponse(news = news, nextPageToken = nextPageToken))
        )
    }

    private fun mockResponseGetNewsError(data: NewsResponse? = null) {
        coEvery {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                cleanCache = any(), fetchFromRemote = any(), offset = any(),
            )
        }.returns(
            if (data == null) Resource.Error(message = "Error on GetNews")
            else Resource.Error(data = data, message = "Load from Cache")
        )
    }

    private fun mockResponseGetNewsErrorThenSuccess(
        news: List<NewsInfo>,
    ) {
        coEvery {
            getNewsUseCase.invoke(
                pageToken = any(), limitPerPage = any(), sortType = any(),
                symbols = any(), startDate = any(), endDate = any(),
                offset = any(), fetchFromRemote = any(), cleanCache = any(),
            )
        }.returnsMany(
            Resource.Error(message = "Error on GetNews"),
            Resource.Success(data = NewsResponse(news = news, nextPageToken = null))
        )
    }

    private fun mockNetwork() {
        coEvery {
            connectivityObserver.observe()
        }.returns(
            flow {
                emit(ConnectivityObserver.Status.Lost)
                delay(1000)
                emit(ConnectivityObserver.Status.Available)
            }
        )
    }

    private fun mockGetRealTimeNews(
        news: List<NewsInfo>,
    ) {
        coEvery {
            getRealTimeNewsUseCase.invoke()
        }.returns(
            flow {
                emit(news)
            }
        )
    }
}