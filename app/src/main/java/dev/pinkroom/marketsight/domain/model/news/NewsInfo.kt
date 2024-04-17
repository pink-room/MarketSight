package dev.pinkroom.marketsight.domain.model.news

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


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
    val images: List<ImagesNews>? = null,
){
    fun getImageUrl(imageSize: ImageSize) = images?.find { it.size == imageSize }?.url

    fun getUpdatedDateFormatted(): String{
        val formatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())

        return updatedAt.format(formatter)
    }
}
