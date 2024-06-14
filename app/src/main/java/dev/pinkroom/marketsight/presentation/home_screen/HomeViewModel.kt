package dev.pinkroom.marketsight.presentation.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.AssetFilter
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.use_case.assets.GetAllAssetsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllAssetsUseCase: GetAllAssetsUseCase,
    private val dispatchers: DispatcherProvider,
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _action = Channel<HomeAction>()
    val action = _action.receiveAsFlow()

    private var stocksList: List<Asset> = emptyList()
    private var cryptosList: List<Asset> = emptyList()
    private var selectedFilter = uiState.value.filters.find { it.isSelected } ?: uiState.value.filters.first()

    private var searchJob: Job? = null

    init {
        getAllAssets()
    }

    fun onEvent(event: HomeEvent) {
        when(event){
            is HomeEvent.NewSearchInput -> changeSearchInput(newInput = event.value)
            is HomeEvent.ChangeAssetFilter -> changeAssetFilter(filterToBeSelected = event.assetSelected)
            HomeEvent.RetryToGetAssetList -> retryToGetAssetList()
            HomeEvent.Refresh -> refresh()
        }
    }

    private fun changeSearchInput(newInput: String) {
        _uiState.update { it.copy(searchInput = newInput) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            val newList = selectedFilter.typeAsset.getAssets().filter {
                it.name.lowercase().startsWith(newInput.lowercase()) || it.symbol.startsWith(
                    newInput.uppercase()
                )
            }
            _uiState.update { it.copy(assets = newList, isEmptyOnSearch = newList.isEmpty()) }
        }
    }

    private fun changeAssetFilter(filterToBeSelected: AssetFilter) {
        searchJob?.cancel()
        val newFilters = uiState.value.filters.map {
            if (it == filterToBeSelected) it.copy(isSelected = true)
            else it.copy(isSelected = false)
        }
        selectedFilter = filterToBeSelected
        val assets = filterToBeSelected.typeAsset.getAssets()
        _uiState.update {
            it.copy(
                filters = newFilters,
                placeHolder = filterToBeSelected.placeHolder,
                searchInput = null,
                isEmptyOnSearch = false,
                hasError = assets.isEmpty(),
                assets = assets,
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getAllAssets() {
        viewModelScope.launch(dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            val allStocksAssetsRequest = async { getAllAssetsUseCase(typeAsset = TypeAsset.Stock) }
            val allCryptoAssetsRequest = async { getAllAssetsUseCase(typeAsset = TypeAsset.Crypto) }
            awaitAll(allStocksAssetsRequest, allCryptoAssetsRequest)

            stocksList = extractAssetsFromResult(result = allStocksAssetsRequest.getCompleted(), typeAsset = TypeAsset.Stock)
            cryptosList = extractAssetsFromResult(result = allCryptoAssetsRequest.getCompleted(), typeAsset = TypeAsset.Crypto)

            val assets = selectedFilter.typeAsset.getAssets()
            val hasError = assets.isEmpty()
            _uiState.update { it.copy(isLoading = false, assets = assets, hasError = hasError) }
        }
    }

    private fun extractAssetsFromResult(result: Resource<List<Asset>>, typeAsset: TypeAsset? = null): List<Asset> {
        return when(result){
            is Resource.Success -> result.data
            is Resource.Error -> {
                val storedAssets = if (typeAsset != null) {
                    typeAsset.getAssets()
                } else {
                    val assets = selectedFilter.typeAsset.getAssets()
                    if (assets.isNotEmpty())
                        _action.trySend(HomeAction.ShowSnackBar(message = R.string.error_on_getting_assets))
                    assets
                }
                storedAssets
            }
        }
    }

    private fun TypeAsset.getAssets() = when(this) {
        TypeAsset.Crypto -> cryptosList
        TypeAsset.Stock -> stocksList
    }

    private fun retryToGetAssetList() {
        viewModelScope.launch(dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            val response = getAllAssetsUseCase(typeAsset = selectedFilter.typeAsset, fetchFromRemote = true)
            val assets = extractAssetsFromResult(result = response)

            when(selectedFilter.typeAsset) {
                TypeAsset.Crypto -> cryptosList = assets
                TypeAsset.Stock -> stocksList = assets
            }
            _uiState.update { it.copy(isLoading = false, assets = assets, hasError = assets.isEmpty(), isRefreshing = false) }
        }
    }

    private fun refresh() {
        _uiState.update { it.copy(isRefreshing = true, searchInput = null) }
        retryToGetAssetList()
    }

}