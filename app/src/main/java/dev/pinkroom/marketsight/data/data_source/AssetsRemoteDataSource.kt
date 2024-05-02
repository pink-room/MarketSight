package dev.pinkroom.marketsight.data.data_source

import dev.pinkroom.marketsight.common.Constants
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.common.formatToStandardIso
import dev.pinkroom.marketsight.data.mapper.toQuotesResponseDto
import dev.pinkroom.marketsight.data.mapper.toTradesResponseDto
import dev.pinkroom.marketsight.data.remote.AlpacaCryptoApi
import dev.pinkroom.marketsight.data.remote.AlpacaPaperApi
import dev.pinkroom.marketsight.data.remote.AlpacaStockApi
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.BarAssetDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuotesResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradesResponseDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_paper_api.AssetDto
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.model.bars_asset.TimeFrame
import java.time.LocalDateTime
import javax.inject.Inject

class AssetsRemoteDataSource @Inject constructor(
    private val alpacaPaperApi: AlpacaPaperApi,
    private val alpacaStockApi: AlpacaStockApi,
    private val alpacaCryptoApi: AlpacaCryptoApi,
) {
    suspend fun getAllAssets(typeAsset: TypeAsset): List<AssetDto> {
        return alpacaPaperApi.getAssets(typeAsset = typeAsset.value)
    }

    suspend fun getBars(
        symbol: String,
        typeAsset: TypeAsset,
        timeFrame: TimeFrame? = TimeFrame.Day,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sort: SortType? = SortType.ASC,
    ): List<BarAssetDto> {
        return if (typeAsset is TypeAsset.Stock)
            alpacaStockApi.getHistoricalBarsStock(
                symbol = symbol,
                startDate = startDate.formatToStandardIso(),
                endDate = endDate.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                timeFrame = timeFrame?.frameValue,
            ).bars
        else
            alpacaCryptoApi.getHistoricalBarsCrypto(
                symbol = symbol,
                startDate = startDate.formatToStandardIso(),
                endDate = endDate.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                timeFrame = timeFrame?.frameValue,
            ).bars.entries.first().value
    }

    suspend fun getTrades(
        symbol: String,
        typeAsset: TypeAsset,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        sort: SortType? = SortType.ASC,
        pageToken: String? = null,
    ): TradesResponseDto {
        return if (typeAsset is TypeAsset.Stock)
            alpacaStockApi.getHistoricalTradesStock(
                symbol = symbol,
                startDate = startDate?.formatToStandardIso(),
                endDate = endDate?.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                pageToken = pageToken
            )
        else
            alpacaCryptoApi.getHistoricalTradesCrypto(
                symbol = symbol,
                startDate = startDate?.formatToStandardIso(),
                endDate = endDate?.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                pageToken = pageToken
            ).toTradesResponseDto()
    }

    suspend fun getQuotes(
        symbol: String,
        typeAsset: TypeAsset,
        limit: Int? = Constants.DEFAULT_LIMIT_ASSET,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        sort: SortType? = SortType.ASC,
        pageToken: String? = null,
    ): QuotesResponseDto {
        return if (typeAsset is TypeAsset.Stock)
            alpacaStockApi.getHistoricalQuotesStock(
                symbol = symbol,
                startDate = startDate?.formatToStandardIso(),
                endDate = endDate?.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                pageToken = pageToken
            )
        else
            alpacaCryptoApi.getHistoricalQuotesCrypto(
                symbol = symbol,
                startDate = startDate?.formatToStandardIso(),
                endDate = endDate?.formatToStandardIso(),
                sort = sort?.type,
                limit = limit,
                pageToken = pageToken
            ).toQuotesResponseDto()
    }
}