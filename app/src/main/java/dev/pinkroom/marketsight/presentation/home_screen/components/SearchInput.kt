package dev.pinkroom.marketsight.presentation.home_screen.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.presentation.core.theme.Gray
import dev.pinkroom.marketsight.presentation.core.theme.GrayAthens
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.core.theme.shimmerEffect

@Composable
fun SearchInput(
    modifier: Modifier = Modifier,
    value: String,
    placeHolder: String,
    isLoading: Boolean,
    isEnabled: Boolean,
    onChangeInput: (String) -> Unit,
    closeInput: () -> Unit,
){
    val colorPlaceHolder = when(isEnabled) {
        true -> if (isSystemInDarkTheme()) GrayAthens else Gray
        false -> Gray
    }
    if (!isLoading)
        OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = onChangeInput,
            enabled = isEnabled,
            singleLine = true,
            shape = CircleShape,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_search),
                    contentDescription = null,
                    tint = colorPlaceHolder,
                )
            },
            placeholder = {
                Text(
                    text = placeHolder,
                    color = colorPlaceHolder,
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onAny = {
                    closeInput()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.background,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                selectionColors = TextSelectionColors(
                    handleColor = MaterialTheme.colorScheme.onPrimary,
                    backgroundColor = MaterialTheme.colorScheme.background,
                )
            ),
        )
    else
        Box(
            modifier = modifier
                .height(dimens.searchInputHeight)
                .clip(CircleShape)
                .shimmerEffect()
        )
}