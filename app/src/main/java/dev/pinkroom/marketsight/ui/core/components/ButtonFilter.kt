package dev.pinkroom.marketsight.ui.core.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.ui.core.theme.BabyBlue
import dev.pinkroom.marketsight.ui.core.theme.Blue
import dev.pinkroom.marketsight.ui.core.theme.dimens

@Composable
fun ButtonFilter(
    isSelected: Boolean,
    text: String,
    onClick: () -> Unit,
){
    val colorBackground = if (isSelected) BabyBlue.copy(alpha = 0.4f) else Color.Transparent
    val colorContent = if (isSelected) Blue else MaterialTheme.colorScheme.onBackground
    val colorBorder = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.onBackground
    Button(
        shape = RoundedCornerShape(dimens.smallShape),
        border = BorderStroke(
            width = dimens.smallWidth, color = colorBorder,
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorBackground,
            contentColor = colorContent,
        ),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(dimens.xSmallPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isSelected)
                Icon(
                    modifier = Modifier
                        .size(dimens.smallIconSize),
                    painter = painterResource(id = R.drawable.icon_check),
                    contentDescription = null,
                )

            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}