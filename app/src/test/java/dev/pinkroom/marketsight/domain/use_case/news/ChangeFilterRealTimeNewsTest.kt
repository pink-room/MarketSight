package dev.pinkroom.marketsight.domain.use_case.news

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import dev.pinkroom.marketsight.util.MainCoroutineRule
import dev.pinkroom.marketsight.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ChangeFilterRealTimeNewsTest{

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val dispatchers = TestDispatcherProvider()
    private val newsRepository = mockk<NewsRepository>(relaxed = true, relaxUnitFun = true)
    private val changeFilterRealTimeNewsUseCase = ChangeFilterRealTimeNewsUseCase(
        newsRepository = newsRepository,
        dispatchers = dispatchers,
    )

    @Test
    fun `Given params, then call changeFilterNews and return list with subscribed symbols`() = runTest {
        // GIVEN
        val subscribeSymbols = listOf("TSLA","AAPL")
        val unsubscribeSymbols = listOf("AAA")
        mockChangeFilterNewsSuccess(subscribeSymbols)

        // WHEN
        val response = changeFilterRealTimeNewsUseCase(
            subscribeSymbols = subscribeSymbols,
            unsubscribeSymbols = unsubscribeSymbols
        ).toList()

        // THEN
        coVerify {
            newsRepository.changeFilterRealTimeNews(
                symbols = unsubscribeSymbols,
                actionAlpaca = ActionAlpaca.Unsubscribe,
            )
        }
        coVerify {
            newsRepository.changeFilterRealTimeNews(
                symbols = subscribeSymbols,
                actionAlpaca = ActionAlpaca.Subscribe,
            )
        }
        assertThat(response).isNotEmpty()
        response.forEachIndexed { index, it ->
            assertThat(it is Resource.Success).isTrue()
            val data = it as Resource.Success
            val expectedResult = if (index == response.size) emptyList()
            else subscribeSymbols
            assertThat(data.data).isEqualTo(expectedResult)
        }
    }

    @Test
    fun `Given params, then call changeFilterNews and return error`() = runTest {
        // GIVEN
        val subscribeSymbols = listOf("TSLA","AAPL")
        val unsubscribeSymbols = listOf("AAA")
        mockChangeFilterNewsError(
            subscribedSymbols = subscribeSymbols,
            unsubscribedSymbols = unsubscribeSymbols
        )

        // WHEN
        val response = changeFilterRealTimeNewsUseCase(
            subscribeSymbols = subscribeSymbols,
            unsubscribeSymbols = unsubscribeSymbols
        ).last()

        // THEN
        coVerify {
            newsRepository.changeFilterRealTimeNews(
                symbols = unsubscribeSymbols,
                actionAlpaca = ActionAlpaca.Unsubscribe,
            )
        }
        coVerify {
            newsRepository.changeFilterRealTimeNews(
                symbols = subscribeSymbols,
                actionAlpaca = ActionAlpaca.Subscribe,
            )
        }
        assertThat(response is Resource.Error).isTrue()
        val data = response as Resource.Error
        val expectedResult =  unsubscribeSymbols + subscribeSymbols
        assertThat(data.data).isEqualTo(expectedResult)
    }

    private fun mockChangeFilterNewsSuccess(
        subscribedSymbols: List<String>,
    ){
        coEvery { newsRepository.changeFilterRealTimeNews(any(),any()) }.returnsMany(
            Resource.Success(data = emptyList()), Resource.Success(data = subscribedSymbols)
        )
    }

    private fun mockChangeFilterNewsError(
        subscribedSymbols: List<String>,
        unsubscribedSymbols: List<String>,
    ){
        coEvery { newsRepository.changeFilterRealTimeNews(any(),any()) }.returns(
            Resource.Error(data = subscribedSymbols + unsubscribedSymbols)
        )
    }
}