package dev.pinkroom.marketsight.domain.model

import java.time.LocalDateTime

data class NewsInfo(
    val id: Long,
    val headline: String,
    val summary: String,
    val author: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val url: String,
    val symbols: List<String>,
    val source: String,
)
