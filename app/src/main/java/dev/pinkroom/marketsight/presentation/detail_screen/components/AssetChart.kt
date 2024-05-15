package dev.pinkroom.marketsight.presentation.detail_screen.components

import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pinkroom.marketsight.common.formatToString
import dev.pinkroom.marketsight.domain.model.bars_asset.AssetChartInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.BarAsset
import dev.pinkroom.marketsight.presentation.core.theme.Green
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import java.time.LocalDateTime

@Composable
fun AssetChart(
    modifier: Modifier,
    chartInfo: AssetChartInfo,
    graphColor: Color = Green,
) {
    val density = LocalDensity.current

    val transparentGraphColor = remember { graphColor.copy(alpha = 0.5f) }
    val textPaintY = remember(density) {
        Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = Align.LEFT
            textSize = density.run { 12.sp.toPx() }
        }
    }
    val textPaintX = remember(density) {
        Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = Align.CENTER
            textSize = density.run { 10.sp.toPx() }
        }
    }

    val coordinates = mutableListOf<PointF>()
    val controlPoints1 = mutableListOf<PointF>()
    val controlPoints2 = mutableListOf<PointF>()

    Canvas(
        modifier = modifier
            //.background(color = Green)
    ) {
        // Placing x axis info
        val addedYears = mutableListOf<Int>()
        val spacingStart = 2
        val spacePerHour = size.width / (chartInfo.barsInfo.size+1)
        (0 until chartInfo.barsInfo.size).forEach { i ->
            val info = chartInfo.barsInfo[i]
            val hour = info.timestamp.year
            if (!addedYears.contains(hour)) {
                addedYears.add(hour)
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        hour.toString(),
                        ((i+spacingStart) * spacePerHour),
                        size.height - 55,
                        textPaintX
                    )
                }
            }
        }

        // Placing y axis info
        val heightAxisY = size.height + 8
        val maxItemsY = 6
        val yAxisSpace = ( size.height / (maxItemsY+1) )
        val priceStep = (chartInfo.upperValue - chartInfo.lowerValue) / maxItemsY.toFloat()
        (0..maxItemsY).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    (chartInfo.lowerValue + priceStep * i).formatToString(),
                    0f,
                    heightAxisY - yAxisSpace * (i+1),
                    textPaintY
                )
            }
        }

        // Placing points
        val heightPoints = size.height
        val xAxisSpacePoint = (size.width) / (chartInfo.barsInfo.size+1)
        (0 until chartInfo.barsInfo.size).forEach { i ->
            val info = chartInfo.barsInfo[i]
            val lowerValue = chartInfo.lowerValue
            val upperValue = chartInfo.upperValue

            val ratio = (info.closingPrice - lowerValue) / (upperValue - lowerValue)
            val x1 = xAxisSpacePoint * (i + spacingStart)
            val y1 = (heightPoints-yAxisSpace) - (ratio * (heightPoints-yAxisSpace)).toFloat()
            coordinates.add(PointF(x1, y1))

            drawCircle(
                color = Color.Black,
                radius = 6f,
                center = Offset(x1, y1),
            )
        }

        /** calculating the connection points */
        for (i in 1 until coordinates.size) {
            controlPoints1.add(PointF((coordinates[i].x + coordinates[i - 1].x) / 2, coordinates[i - 1].y))
            controlPoints2.add(PointF((coordinates[i].x + coordinates[i - 1].x) / 2, coordinates[i].y))
        }

        // Drawing the path
        val strokePath = Path().apply {
            reset()
            moveTo(coordinates.first().x, coordinates.first().y)
            for (i in 0 until coordinates.size - 1) {
                cubicTo(
                    controlPoints1[i].x,controlPoints1[i].y,
                    controlPoints2[i].x,controlPoints2[i].y,
                    coordinates[i + 1].x,coordinates[i + 1].y
                )
            }
        }

        drawPath(
            path = strokePath,
            color = Color.Red,
            style = Stroke(
                width = 2f,
                cap = StrokeCap.Round
            )
        )

        /** filling the area under the path */
        val fillPath = android.graphics.Path(strokePath.asAndroidPath())
            .asComposePath()
            .apply {
                lineTo(coordinates.last().x, size.height - yAxisSpace)
                lineTo(coordinates.first().x, size.height - yAxisSpace)
                close()
            }
        drawPath(
            fillPath,
            brush = Brush.verticalGradient(
                listOf(
                    Color.Cyan,
                    Color.Transparent,
                ),
                endY = size.height - yAxisSpace
            ),
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
fun AssetCharPreview() {
    val barsAsset = listOf(
        BarAsset(
            closingPrice = 429.29,
            timestamp = LocalDateTime.of(2020,7,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 705.64,
            timestamp = LocalDateTime.of(2020,10,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 668.01,
            timestamp = LocalDateTime.of(2021,1,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 679.83,
            timestamp = LocalDateTime.of(2021,4,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 775.01,
            timestamp = LocalDateTime.of(2021,7,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 1056.86,
            timestamp = LocalDateTime.of(2021,10,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 1077.77,
            timestamp = LocalDateTime.of(2022,1,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 674.5,
            timestamp = LocalDateTime.of(2022,4,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 265.11,
            timestamp = LocalDateTime.of(2022,7,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 123.22,
            timestamp = LocalDateTime.of(2022,10,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 207.41,
            timestamp = LocalDateTime.of(2023,1,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 261.73,
            timestamp = LocalDateTime.of(2023,4,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 250.22,
            timestamp = LocalDateTime.of(2023,7,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 248.46,
            timestamp = LocalDateTime.of(2023,10,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 275.72,
            timestamp = LocalDateTime.of(2024,1,1,4,0,0),
        ),
        BarAsset(
            closingPrice = 371.92,
            timestamp = LocalDateTime.of(2024,4,1,4,0,0),
        ),
    )
    val maxValue = barsAsset.maxOfOrNull { it.closingPrice }!!
    val minValue = barsAsset.minOfOrNull { it.closingPrice }!!
    AssetChart(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.horizontalPadding, vertical = 10.dp)
            .height(380.dp),
        chartInfo = AssetChartInfo(
            upperValue = maxValue,
            lowerValue = minValue,
            barsInfo = barsAsset,
        ),
    )
}