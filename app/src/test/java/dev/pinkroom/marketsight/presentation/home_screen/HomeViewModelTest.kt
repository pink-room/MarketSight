package dev.pinkroom.marketsight.presentation.home_screen

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isLessThan
import assertk.assertions.isNotEmpty
import assertk.assertions.isNull
import assertk.assertions.isTrue
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.data.mapper.toAsset
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.use_case.assets.GetAllAssets
import dev.pinkroom.marketsight.factories.AssetDtoFactory
import dev.pinkroom.marketsight.util.MainCoroutineRule
import dev.pinkroom.marketsight.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val dispatchers = TestDispatcherProvider()
    private val assetFactory = AssetDtoFactory()
    private val getAllAssets = mockk<GetAllAssets>(relaxed = true, relaxUnitFun = true)
    private lateinit var homeViewModel: HomeViewModel

    private fun initViewModel() {
        homeViewModel = HomeViewModel(
            getAllAssets = getAllAssets,
            dispatchers = dispatchers,
        )
    }

    @Test
    fun `When init VM, Then Success on GetAllAssets`() = runTest {
        // GIVEN
        val assets = assetFactory.buildList(number = 500).map { it.toAsset() }
        mockResponseGetAllAssetsSuccess(assets)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        val uiState = homeViewModel.uiState.value

        coVerify(exactly = 2) {
            getAllAssets.invoke(typeAsset = any())
        }
        assertThat(uiState.assets).isNotEmpty()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.hasError).isFalse()
        assertThat(uiState.isEmptyOnSearch).isFalse()
    }

    @Test
    fun `When init VM, Then Success on GetAllAssets of type stock`() = runTest {
        // GIVEN
        val assets = assetFactory.buildList(number = 500).map { it.toAsset() }
        mockResponseGetAllAssetsOfTypeStockSuccess(assets)

        // WHEN
        initViewModel()
        advanceUntilIdle()

        // THEN
        var uiState = homeViewModel.uiState.value
        val activeFilter = uiState.filters.find { it.isSelected } ?: uiState.filters.first()
        if (activeFilter.typeAsset == TypeAsset.Stock){
            assertThat(uiState.assets).isNotEmpty()
            assertThat(uiState.isLoading).isFalse()
            assertThat(uiState.hasError).isFalse()
            val filterCrypto = uiState.filters.find { it.typeAsset == TypeAsset.Crypto }!!
            homeViewModel.onEvent(HomeEvent.ChangeAssetFilter(assetSelected = filterCrypto))
            advanceUntilIdle()
        }

        uiState = homeViewModel.uiState.value
        assertThat(uiState.assets).isEmpty()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.hasError).isTrue()

        coVerify {
            getAllAssets.invoke(typeAsset = any())
        }
    }

    @Test
    fun `Given new search input, When Assets is not empty, Then update values based on input`() = runTest {
        // GIVEN
        val inputSearch = "AA"
        val assets = assetFactory.buildList(number = 500).map { it.toAsset() }
        mockResponseGetAllAssetsSuccess(assets)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        homeViewModel.onEvent(HomeEvent.NewSearchInput(value = inputSearch))
        advanceUntilIdle()

        // THEN
        val uiState = homeViewModel.uiState.value
        assertThat(uiState.searchInput).isEqualTo(inputSearch)
        assertThat(uiState.assets).isNotEmpty()
        assertThat(uiState.isEmptyOnSearch).isFalse()
        assertThat(uiState.assets.size).isLessThan(assets.size)
    }

    @Test
    fun `Given new search input, When Assets is not empty, Then no response for the given search input`() = runTest {
        // GIVEN
        val inputSearch = "AABXMAOPIASMMABSYNAONS123"
        val assets = assetFactory.buildList(number = 500).map { it.toAsset() }
        mockResponseGetAllAssetsSuccess(assets)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        homeViewModel.onEvent(HomeEvent.NewSearchInput(value = inputSearch))
        advanceUntilIdle()

        // THEN
        val uiState = homeViewModel.uiState.value
        assertThat(uiState.searchInput).isEqualTo(inputSearch)
        assertThat(uiState.assets).isEmpty()
        assertThat(uiState.isEmptyOnSearch).isTrue()
    }

    @Test
    fun `When change Asset filter, Then show assets related to filter selected`() = runTest {
        // GIVEN
        val assets = assetFactory.buildList(number = 500).map { it.toAsset() }
        mockResponseGetAllAssetsSuccess(assets)
        initViewModel()
        advanceUntilIdle()
        val filterCrypto = homeViewModel.uiState.value.filters.find { it.typeAsset == TypeAsset.Crypto }!!

        // WHEN
        homeViewModel.onEvent(HomeEvent.ChangeAssetFilter(assetSelected = filterCrypto))
        advanceUntilIdle()

        // THEN
        val uiState = homeViewModel.uiState.value
        val newSelectedFilter = uiState.filters.find { it.isSelected }
        assertThat(uiState.searchInput).isNull()
        assertThat(uiState.assets).isNotEmpty()
        assertThat(uiState.isEmptyOnSearch).isFalse()
        assertThat(uiState.hasError).isFalse()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.assets.first().isStock).isFalse()
        assertThat(newSelectedFilter!!.typeAsset).isEqualTo(TypeAsset.Crypto)
        assertThat(uiState.filters.filter { it.isSelected }.size).isEqualTo(1)
    }

    @Test
    fun `When Retry To Get Assets, Then on Success update assets`() = runTest {
        // GIVEN
        val assets = assetFactory.buildList(number = 500).map { it.toAsset() }
        mockResponseGetAllAssetsFirstWithErrorAndThenSuccess(assets)
        initViewModel()
        advanceUntilIdle()

        // WHEN
        var uiState = homeViewModel.uiState.value
        assertThat(uiState.assets).isEmpty()
        assertThat(uiState.hasError).isTrue()
        assertThat(uiState.isEmptyOnSearch).isFalse()
        homeViewModel.onEvent(HomeEvent.RetryToGetAssetList)
        advanceUntilIdle()

        // THEN
        uiState = homeViewModel.uiState.value
        assertThat(uiState.assets).isNotEmpty()
        assertThat(uiState.hasError).isFalse()
        assertThat(uiState.isLoading).isFalse()
    }

    private fun mockResponseGetAllAssetsSuccess(assets: List<Asset>) {
        coEvery {
            getAllAssets.invoke(typeAsset = TypeAsset.Crypto)
        } returns Resource.Success(data = assets.filter { !it.isStock })

        coEvery {
            getAllAssets.invoke(typeAsset = TypeAsset.Stock)
        } returns Resource.Success(data = assets.filter { it.isStock })
    }

    private fun mockResponseGetAllAssetsOfTypeStockSuccess(assets: List<Asset>) {
        coEvery {
            getAllAssets.invoke(typeAsset = TypeAsset.Crypto)
        } returns Resource.Error()

        coEvery {
            getAllAssets.invoke(typeAsset = TypeAsset.Stock)
        } returns Resource.Success(data = assets.filter { it.isStock })
    }

    private fun mockResponseGetAllAssetsFirstWithErrorAndThenSuccess(assets: List<Asset>) {
        coEvery {
            getAllAssets.invoke(typeAsset = any())
        }.returnsMany(
            Resource.Error(),
            Resource.Success(data = assets),
        )
    }

}