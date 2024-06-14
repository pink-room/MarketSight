package dev.pinkroom.marketsight.data.mapper

import dev.pinkroom.marketsight.data.local.entity.AssetEntity
import dev.pinkroom.marketsight.data.local.entity.ImagesEntity
import dev.pinkroom.marketsight.data.local.entity.NewsEntity
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.ImagesNewsDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsDto
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsResponseDto
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.news.ImagesNews
import dev.pinkroom.marketsight.domain.model.news.NewsInfo

fun AssetEntity.toAsset() = Asset(
    id = id,
    name = name,
    symbol = symbol,
    exchange = exchange,
    isStock = type != "crypto",
)

fun NewsResponseDto.toNewsMap() = news.associate {
    it.toNewsEntity() to it.images.map { image -> image.toImageEntity() }
}

fun NewsDto.toNewsEntity() = NewsEntity(
    author = this.author,
    createdAt = this.createdAt,
    headline = this.headline,
    id = this.id,
    source = this.source,
    summary = this.summary,
    symbols = this.symbols,
    updatedAt = this.updatedAt,
    url = this.url,
)

fun ImagesNewsDto.toImageEntity() = ImagesEntity(
    size = this.size,
    url = this.url,
)

fun NewsEntity.toNewsInfo(images: List<ImagesEntity>) = NewsInfo(
    author = this.author,
    createdAt = this.createdAt.toLocalDateTime(),
    headline = this.headline,
    id = this.id,
    source = this.source,
    summary = this.summary,
    symbols = this.symbols,
    updatedAt = this.updatedAt.toLocalDateTime(),
    url = this.url,
    images = images.map { it.toImagesNews() }
)

fun ImagesEntity.toImagesNews() = ImagesNews(
    url = this.url,
    size = this.size.toImageSize(),
)