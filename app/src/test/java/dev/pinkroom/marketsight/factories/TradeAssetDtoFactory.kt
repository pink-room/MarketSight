package dev.pinkroom.marketsight.factories

import com.github.javafaker.Faker
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.TradeAssetDto
import kotlin.random.Random

class TradeAssetDtoFactory: BaseFactory<TradeAssetDto> {

    private val faker = Faker()
    override fun build() = TradeAssetDto(
        type = if (Random.nextInt() % 2 == 0) "us_equity" else "crypto",
        tradePrice = faker.number().randomDouble(1000,1,10000000),
        tradeId = faker.number().randomNumber(),
        tradeSize = faker.number().randomDouble(100,1,1000000),
        dateTransaction = "2024-05-03T16:58:38.422833437Z",
    )

    fun buildList(number: Int, type: String? = null, symbol: String? = null) = List(number) {
        build().copy(type = type, symbol = symbol)
    }
}