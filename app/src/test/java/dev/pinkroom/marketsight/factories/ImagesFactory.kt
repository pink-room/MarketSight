package dev.pinkroom.marketsight.factories

import com.github.javafaker.Faker
import dev.pinkroom.marketsight.data.remote.model.dto.alpaca_data_api.ImagesNewsDto
import kotlin.random.Random

class ImagesFactory: BaseFactory<ImagesNewsDto> {

    private val faker = Faker()
    override fun build(): ImagesNewsDto = ImagesNewsDto(
        size = getImageSizeRandom(),
        url = faker.internet().url()
    )

    private fun getImageSizeRandom(): String {
        val sizeValues = listOf("large","small","thumb")
        return sizeValues[Random.nextInt(from = 0, until = sizeValues.size)]
    }
}