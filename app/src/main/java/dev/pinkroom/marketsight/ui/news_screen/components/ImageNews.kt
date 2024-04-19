package dev.pinkroom.marketsight.ui.news_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.SubcomposeAsyncImage
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.ui.core.theme.shimmerEffect

@Composable
fun ImageNews(
    modifier: Modifier = Modifier,
    url: String? = null,
){
    SubcomposeAsyncImage(
        model = url,
        contentDescription = null,
        error = {
            Image(
                painter = painterResource(id = R.drawable.default_news_thumb),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        },
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmerEffect()
            )
        },
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}