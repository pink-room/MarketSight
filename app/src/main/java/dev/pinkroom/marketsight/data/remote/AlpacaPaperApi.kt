package dev.pinkroom.marketsight.data.remote

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_paper_api.AssetDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AlpacaPaperApi {
    @GET("assets")
    suspend fun getAssets(
        @Query("asset_class") typeAsset: String? = null,
        @Query("status") status: String? = "active",
    ): List<AssetDto>
}