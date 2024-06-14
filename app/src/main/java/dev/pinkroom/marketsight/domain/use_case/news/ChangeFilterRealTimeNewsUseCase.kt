package dev.pinkroom.marketsight.domain.use_case.news

import dev.pinkroom.marketsight.common.ActionAlpaca
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.domain.repository.NewsRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChangeFilterRealTimeNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val dispatchers: DispatcherProvider,
){
    operator fun invoke(
        subscribeSymbols: List<String>? = null,
        unsubscribeSymbols: List<String>? = null,
    ) = flow<Resource<List<String>>> {
        val symbolsToRevert = mutableListOf<String>()
        unsubscribeSymbols?.let { symbols ->
            val response = newsRepository.changeFilterRealTimeNews(symbols = symbols, actionAlpaca = ActionAlpaca.Unsubscribe)
            when(response){
                is Resource.Error -> {
                    symbolsToRevert.addAll(unsubscribeSymbols)
                }
                else -> Unit
            }
        }

        subscribeSymbols?.let { symbols ->
            val response = newsRepository.changeFilterRealTimeNews(symbols = symbols, actionAlpaca = ActionAlpaca.Subscribe)
            when(response){
                is Resource.Success -> {
                    emit(Resource.Success(data = response.data))
                }
                is Resource.Error -> {
                    symbolsToRevert.addAll(subscribeSymbols)
                }
            }
        } ?: run {
            if (symbolsToRevert.isEmpty()) emit(Resource.Success(data = emptyList()))
        }

        if (symbolsToRevert.isNotEmpty()) emit(Resource.Error(data = symbolsToRevert))
    }.flowOn(dispatchers.IO)
}