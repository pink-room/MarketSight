package dev.pinkroom.marketsight.factories

import com.github.javafaker.Faker
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_paper_api.AssetDto
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import kotlin.random.Random

class AssetDtoFactory: BaseFactory<AssetDto> {

    private val faker = Faker()
    override fun build() = AssetDto(
        id = faker.number().randomNumber().toString(),
        type = if (Random.nextInt() % 2 == 0) "us_equity" else "crypto",
        symbol = faker.stock().nsdqSymbol(),
        name = faker.stock().nsdqSymbol(),
        exchange = "NASDAQ",
    )

    fun listAssets(number: Int, type: TypeAsset) = List(number) { build(type = type) }

    private fun build(type: TypeAsset) = AssetDto(
        id = faker.number().randomNumber().toString(),
        type = type.value,
        symbol = faker.stock().nsdqSymbol(),
        name = faker.stock().nsdqSymbol(),
        exchange = "NASDAQ",
    )
}