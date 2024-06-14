package dev.pinkroom.marketsight.factories

import com.github.javafaker.Faker
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_service.NewsMessageDto

class NewsFactory: BaseFactory<NewsMessageDto> {

    private val faker = Faker()
    override fun build() = NewsMessageDto(
        author = faker.name().fullName(),
        symbols = listOf(faker.stock().nsdqSymbol()),
        url = faker.internet().url(),
        updatedAt = "2024-04-11T13:45:17Z",
        summary = faker.lorem().sentence(),
        source = faker.lorem().word(),
        id = faker.number().randomNumber(),
        headline = faker.lorem().sentence(),
        createdAt = "2024-04-11T13:45:17Z",
        type = "n",
    )

}