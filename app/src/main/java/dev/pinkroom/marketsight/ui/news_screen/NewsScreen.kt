package dev.pinkroom.marketsight.ui.news_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
){
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Text(
            text = "News Screen",
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun NewsScreenPreview(){
    NewsScreen()
}