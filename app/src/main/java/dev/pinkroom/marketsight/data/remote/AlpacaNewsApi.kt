package dev.pinkroom.marketsight.data.remote

import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AlpacaNewsApi {
    @GET("news")
    suspend fun getNews(
        @Query("symbols") symbols: String? = null,
        @Query("limit") perPage: Int? = null,
        @Query("page_token") pageToken: String? = null,
        @Query("sort") sort: String? = null,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null,
    ): NewsResponseDto
}