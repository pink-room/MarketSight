package dev.pinkroom.marketsight.domain.model.news

sealed interface ImageSize {
    data object Large: ImageSize
    data object Small: ImageSize
    data object Thumb: ImageSize
}