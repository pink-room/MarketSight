package dev.pinkroom.marketsight.data.data_source

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import dev.pinkroom.marketsight.data.local.DbTransaction
import dev.pinkroom.marketsight.data.local.dao.AssetDao
import dev.pinkroom.marketsight.data.local.entity.AssetEntity
import dev.pinkroom.marketsight.data.mapper.toAssetEntity
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.factories.AssetDtoFactory
import dev.pinkroom.marketsight.util.MainCoroutineRule
import dev.pinkroom.marketsight.util.mockAndExecuteTransaction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssetsLocalDataSourceTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val assetDtoFactory = AssetDtoFactory()
    private val assetDao = mockk<AssetDao>(relaxUnitFun = true, relaxed = true)
    private val dbTransaction = mockk<DbTransaction>()
    private val assetsLocalDataSource = AssetsLocalDataSource(
        dbTransaction = dbTransaction,
        assetDao = assetDao,
    )

    @Before
    fun setup() = runTest {
        dbTransaction.mockAndExecuteTransaction()
    }

    @Test
    fun `When cacheAllAssets, Then delete, insert and return list of assets cached`() = runTest {
        // GIVEN
        val type = TypeAsset.Stock
        val assetsEntity = assetDtoFactory.listAssets(number = 250, type = type).map { it.toAssetEntity() }
        mockResponseGetAssetsDao(data = assetsEntity)

        // WHEN
        val response = assetsLocalDataSource.cacheAssets(data = assetsEntity, typeAsset = type)

        // THEN
        coVerify { assetDao.getAllAssetsOfType(typeAsset = type.value) }
        coVerify { assetDao.clearAllOfType(typeAsset = type.value) }
        coVerify { assetDao.insert(assets = assetsEntity) }

        assertThat(response).isNotEmpty()
        assertThat(response).isEqualTo(assetsEntity)
    }

    @Test
    fun `When getAllAssetsOfType, Then return list of assets cached`() = runTest {
        // GIVEN
        val type = TypeAsset.Stock
        val assetsEntity = assetDtoFactory.listAssets(number = 250, type = type).map { it.toAssetEntity() }
        mockResponseGetAssetsDao(data = assetsEntity, typeAsset = type)

        // WHEN
        val response = assetsLocalDataSource.getAllAssetsOfType(typeAsset = type)

        // THEN
        coVerify { assetDao.getAllAssetsOfType(typeAsset = type.value) }

        assertThat(response).isNotEmpty()
        assertThat(response).isEqualTo(assetsEntity.filter { it.type == type.value })
    }

    private fun mockResponseGetAssetsDao(
        data: List<AssetEntity>,
        typeAsset: TypeAsset? = null,
    ){
        coEvery {
            assetDao.getAllAssetsOfType(typeAsset = any())
        }.returns(
            if (typeAsset != null) data.filter { it.type == typeAsset.value }
            else data
        )
    }
}