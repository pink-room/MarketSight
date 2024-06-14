package dev.pinkroom.marketsight.data.data_source

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import dev.pinkroom.marketsight.data.remote.AlpacaPaperApi
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.factories.AssetDtoFactory
import dev.pinkroom.marketsight.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssetsRemoteDataSourceTest{
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val assetDtoFactory = AssetDtoFactory()
    private val alpacaPaperApi = mockk<AlpacaPaperApi>()
    private val assetsRemoteDataSource = AssetsRemoteDataSource(
        alpacaPaperApi = alpacaPaperApi
    )

    @Test
    fun `When call getAllAssets, Then return list of assets`() = runTest {
        // GIVEN
        val type = TypeAsset.Stock
        mockResponseGetAssetsPaperApi(typeAsset = type)

        // WHEN
        val response = assetsRemoteDataSource.getAllAssets(typeAsset = type)

        // THEN
        coVerify { alpacaPaperApi.getAssets(typeAsset = type.value, status = any()) }
        assertThat(response).isNotEmpty()
        response.forEach {
            assertThat(it.type).isEqualTo(type.value)
        }
    }

    private fun mockResponseGetAssetsPaperApi(
        typeAsset: TypeAsset,
    ){
        coEvery {
            alpacaPaperApi.getAssets(typeAsset = any(), status = any())
        }.returns(
            assetDtoFactory.listAssets(number = 250, type = typeAsset)
        )
    }
}