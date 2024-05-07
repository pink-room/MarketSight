package dev.pinkroom.marketsight.factories

import com.github.javafaker.Faker
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_api.BarAssetDto

class BarAssetDtoFactory: BaseFactory<BarAssetDto> {

    private val faker = Faker()
    override fun build() = BarAssetDto(
        tradeCountInBar = faker.number().randomDigit(),
        barVolume = faker.number().randomDouble(10,1,1000),
        timestamp = "2024-04-11T13:45:17.0231232021Z",
        lowPrice = faker.number().randomDouble(10,1,1000),
        highPrice = faker.number().randomDouble(10,1,1000),
        closingPrice = faker.number().randomDouble(10,1,1000),
        openingPrice = faker.number().randomDouble(10,1,1000),
        volumeWeightedAvgPrice = faker.number().randomDouble(10,1,1000),
    )

    fun buildList(number: Int, symbol: String, type: String? = null) = List(number){
        build().copy(symbol = symbol, type = type)
    }
}