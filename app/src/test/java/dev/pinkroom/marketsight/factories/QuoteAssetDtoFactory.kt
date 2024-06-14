package dev.pinkroom.marketsight.factories

import com.github.javafaker.Faker
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.QuoteAssetDto
import kotlin.random.Random

class QuoteAssetDtoFactory: BaseFactory<QuoteAssetDto> {

    private val faker = Faker()
    override fun build() = QuoteAssetDto(
        type = if (Random.nextInt() % 2 == 0) "us_equity" else "crypto",
        askSize = faker.number().randomDouble(100,1,1000000),
        bidSize = faker.number().randomDouble(100,1,1000000),
        bidPrice = faker.number().randomDouble(100,1,1000000),
        askPrice = faker.number().randomDouble(100,1,1000000),
        requestDate = "2024-05-03T16:58:38.422833437Z",
    )

    fun buildList(number: Int, type: String? = null, symbol: String? = null) = List(number) {
        build().copy(type = type, symbol = symbol)
    }
}