package dev.pinkroom.marketsight.common

object Constants {
    // ANIM TIME
    const val ANIM_TIME_CAROUSEL = 700

    // LIMIT
    const val MAX_ITEMS_CAROUSEL = 5
    const val LIMIT_NEWS = 20
    const val DEFAULT_LIMIT_ASSET = 1000
    const val DEFAULT_LIMIT_QUOTES_ASSET = 30
    const val DEFAULT_LIMIT_TRADES_ASSET = 30
    const val BUFFER_LIST = 5
    const val LIMIT_Y_INFO_CHART = 5

    // TOKEN
    const val DEFAULT_PAGINATION_TOKEN_REQUEST_NEWS = "LOCAL"

    // TEXT
    const val ALL_SYMBOLS = "All"

    // DB
    const val NEWS_ENTITY_NAME = "NewsEntity"
    const val SYMBOL_COLUMN_NAME_NEWS_ENTITY = "symbols"
    const val UPDATE_AT_COLUMN_NAME_NEWS_ENTITY = "updatedAt"
}