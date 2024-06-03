package dev.pinkroom.marketsight.factories

import com.github.javafaker.Faker
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_news_api.NewsDto

class NewsDtoFactory: BaseFactory<NewsDto> {

    private val faker = Faker()
    private val imagesFactory = ImagesFactory()
    override fun build() = NewsDto(
        id = faker.number().randomNumber(),
        headline = faker.lorem().sentence(),
        summary = faker.lorem().paragraph(),
        author = faker.name().fullName(),
        createdAt = "2024-04-11T13:45:17Z",
        updatedAt = "2024-04-11T13:45:17Z",
        url = faker.internet().url(),
        symbols = listOf(faker.stock().nsdqSymbol()),
        source = faker.lorem().word(),
        images = imagesFactory.buildList(),
    )
}