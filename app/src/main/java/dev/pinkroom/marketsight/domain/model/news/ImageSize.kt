package dev.pinkroom.marketsight.domain.model.news

sealed interface ImageSize {
    data object Large: ImageSize
    data object Small: ImageSize
    data object Thumb: ImageSize
}

fun ImageSize.getAspectRatio() = when(this){
    ImageSize.Large -> 2024f / 1536f
    ImageSize.Small -> 1024f / 768f
    ImageSize.Thumb -> 250f / 187f
}