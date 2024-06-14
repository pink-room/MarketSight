package dev.pinkroom.marketsight.presentation.detail_screen.components

import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.PointF
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.Constants.LIMIT_Y_INFO_CHART
import dev.pinkroom.marketsight.common.formatToString
import dev.pinkroom.marketsight.common.toReadableDate
import dev.pinkroom.marketsight.domain.model.bars_asset.AssetChartInfo
import dev.pinkroom.marketsight.domain.model.bars_asset.CoordinatePointChart
import dev.pinkroom.marketsight.presentation.core.theme.Green
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.core.theme.shimmerEffect
import kotlin.math.abs

@Composable
fun AssetChart(
    modifier: Modifier,
    chartInfo: AssetChartInfo,
    graphColor: Color = Green,
    colorText: Color,
    isLoading: Boolean,
    @StringRes errorMessage: Int?,
    infoToShow: (value: Double?, previousValue: Double?) -> Unit,
    onRetry: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val textSizeSmall = dimens.labelSmall
    val spacingStartChartXAxis = dimens.spacingStartChartXAxis.value
    val spacingTopChartYAxis = dimens.spacingTopChartYAxis.value
    val colorPrimary = MaterialTheme.colorScheme.primary
    val colorOnPrimary = MaterialTheme.colorScheme.onPrimary
    val heightBoxInfo = dimens.heightBoxInfoChart.value
    val widthBoxInfo = dimens.widthBoxInfoChart.value
    val cornerRadius = dimens.normalShape.value
    val screenWidth = configuration.screenWidthDp.dp
    val heightBoxCurrentPrice = dimens.heightBoxCurrentPrice.value

    val textPaintY = remember(density) {
        Paint().apply {
            color = colorText.toArgb()
            textAlign = Align.RIGHT
            textSize = density.run { textSizeSmall.toPx() }
        }
    }
    val textPaintInfoDate = remember(density) {
        Paint().apply {
            color = colorPrimary.toArgb()
            textAlign = Align.CENTER
            textSize = density.run { textSizeSmall.toPx() }
        }
    }
    val textPaintInfoCurrentPrice = remember(density) {
        Paint().apply {
            color = colorPrimary.toArgb()
            textAlign = Align.CENTER
            textSize = density.run { textSizeSmall.toPx() }
        }
    }

    var coordinates by rememberSaveable {
        mutableStateOf(listOf<CoordinatePointChart>())
    }

    var isUserDragging by remember {
        mutableStateOf(false)
    }

    var isUserPressing by remember {
        mutableStateOf(false)
    }

    var closestPoint by remember {
        mutableStateOf<CoordinatePointChart?>(null)
    }

    if (isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(size = dimens.normalShape))
                .shimmerEffect()
        )
    } else if (chartInfo.barsInfo.isNotEmpty()) {
        Canvas(
            modifier = modifier
                .pointerInput(key1 = Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { _ ->
                            isUserDragging = true
                        },
                        onHorizontalDrag = { change, _ ->
                            closestPoint = findClosestPoint(points = coordinates, targetX = change.position.x)
                            val pointBefore = pointBefore(point = closestPoint, points = coordinates)
                            infoToShow(closestPoint?.closingPrice, pointBefore?.closingPrice)
                        },
                        onDragEnd = {
                            isUserDragging = false
                            infoToShow(null, null)
                        },
                        onDragCancel = {
                            isUserDragging = false
                            infoToShow(null, null)
                        },
                    )
                }
                .pointerInput(key1 = Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            isUserPressing = true
                            closestPoint = findClosestPoint(points = coordinates, targetX = offset.x)
                            val pointBefore = pointBefore(point = closestPoint, points = coordinates)
                            infoToShow(closestPoint?.closingPrice, pointBefore?.closingPrice)
                            tryAwaitRelease()
                            isUserPressing = false
                            if (!isUserDragging) infoToShow(null, null)
                        }
                    )
                },
        ) {
            val widthX = size.width - spacingStartChartXAxis
            val yAxisHeight = size.height
            val spaceBetweenYAndGraph = 10f

            /** Calculate coordinates of path */
            coordinates = calculateCoordinatesPoints(
                chartInfo = chartInfo,
                spacingStart = spacingStartChartXAxis,
                widthX = widthX,
                yAxisHeight = yAxisHeight,
                yAxisSpaceTop = spacingTopChartYAxis,
            )

            /** Placing y axis info */
            drawYAxisInfo(
                chartInfo = chartInfo,
                yAxisHeight = yAxisHeight,
                yAxisSpaceTop = spacingTopChartYAxis,
                textPaintY = textPaintY,
                spaceStart = spacingStartChartXAxis,
                spaceBetweenYAndGraph = spaceBetweenYAndGraph,
            )

            /** Drawing the path */
            drawPathChart(
                coordinates = coordinates,
                graphColor = graphColor,
                heightAxisY = yAxisHeight,
            )

            /** Drawing the line of current Price */
            drawCurrentLinePrice(
                lastPoint = coordinates.last(),
                spaceStart = spacingStartChartXAxis,
                textPaint = textPaintInfoCurrentPrice,
                spaceBetweenYAndGraph = spaceBetweenYAndGraph,
                color = colorOnPrimary,
                heightBoxCurrentPrice = heightBoxCurrentPrice,
            )

            /** Drawing the line of closest point to user pointer input */
            drawInfoRelatedToPointerInput(
                closestPoint = closestPoint,
                isPressed = isUserPressing || isUserDragging,
                textPaint = textPaintInfoDate,
                height = size.height,
                heightBox = heightBoxInfo,
                widthBox = widthBoxInfo,
                cornerRadius = cornerRadius,
                spaceTop = spacingTopChartYAxis,
                colorCircle = colorPrimary,
                colorLine = colorOnPrimary,
                screenWidth = screenWidth.toPx(),
            )
        }
    } else {
        val stringToShow = errorMessage ?: R.string.not_available_data
        Card(
            modifier = modifier
                .fillMaxSize(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = dimens.lowElevation,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimens.normalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(id = stringToShow),
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                )
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(dimens.smallPadding))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.retry)
                        )
                    }
                }
            }
        }
    }
}

private fun findClosestPoint(points: List<CoordinatePointChart>, targetX: Float): CoordinatePointChart? {
    if (points.isEmpty()) return null
    return points.minByOrNull { abs(it.coordinates.x - targetX) }
}

private fun pointBefore(point: CoordinatePointChart?, points: List<CoordinatePointChart>): CoordinatePointChart? {
    if (point == null) return null
    val indexPoint = points.indexOf(point)
    val pointBefore = points.getOrNull(indexPoint-1)
    return pointBefore ?: point
}

private fun calculateCoordinatesPoints(
    chartInfo: AssetChartInfo,
    widthX: Float,
    spacingStart: Float,
    yAxisHeight: Float,
    yAxisSpaceTop: Float,
): List<CoordinatePointChart> {
    val coordinates = mutableListOf<CoordinatePointChart>()
    val xAxisSpacePoint = widthX / (chartInfo.barsInfo.size-1)
    val heightWithSpaceTop = yAxisHeight - yAxisSpaceTop
    chartInfo.barsInfo.forEachIndexed { i, _ ->
        val info = chartInfo.barsInfo[i]
        val lowerValue = chartInfo.lowerValue
        val upperValue = chartInfo.upperValue

        val ratio = (info.closingPrice - lowerValue) / (upperValue - lowerValue)
        val x1 = spacingStart + (xAxisSpacePoint * i)
        val y1 = if (ratio.isNaN()) heightWithSpaceTop/2 else heightWithSpaceTop - (ratio * heightWithSpaceTop).toFloat() + yAxisSpaceTop
        coordinates.add(
            CoordinatePointChart(
                coordinates = PointF(x1, y1),
                closingPrice = info.closingPrice,
                timestamp = info.timestamp,
            )
        )
    }
    return coordinates.toList()
}

private fun DrawScope.drawCurrentLinePrice(
    lastPoint: CoordinatePointChart,
    spaceStart: Float,
    textPaint: Paint,
    spaceBetweenYAndGraph: Float,
    heightBoxCurrentPrice: Float,
    color: Color,
) {
    val widthStroke = 3f
    val cornerRadius = 12f
    val width = size.width
    val coordinatesLastPoint = lastPoint.coordinates

    val lineCurrentPrice = Path().apply {
        reset()
        moveTo(x = spaceStart, y = coordinatesLastPoint.y)
        lineTo(x = width, y = coordinatesLastPoint.y)
    }

    drawPath(
        path = lineCurrentPrice,
        color = color,
        style = Stroke(
            width = widthStroke,
            cap = StrokeCap.Square,
        )
    )

    drawRoundRect(
        color = color,
        size = Size(width = spaceStart-spaceBetweenYAndGraph, height = heightBoxCurrentPrice),
        topLeft = Offset(x = 0f, y = coordinatesLastPoint.y - heightBoxCurrentPrice/2),
        cornerRadius = CornerRadius(x = cornerRadius),
    )

    drawContext.canvas.nativeCanvas.apply {
        drawText(
            lastPoint.closingPrice.formatToString(),
            (spaceStart-spaceBetweenYAndGraph)/2,
            coordinatesLastPoint.y + textPaint.textSize/3,
            textPaint,
        )
    }
}

private fun DrawScope.drawInfoRelatedToPointerInput(
    closestPoint: CoordinatePointChart?,
    isPressed: Boolean,
    textPaint: Paint,
    height: Float,
    widthBox: Float,
    heightBox: Float,
    cornerRadius: Float,
    spaceTop: Float,
    colorCircle: Color,
    colorLine: Color,
    screenWidth: Float,
) {
    val radiusCircle = 15f
    val widthStroke = 6f
    if (isPressed && closestPoint != null) {
        val coordinatesInput = closestPoint.coordinates
        val neededSpaceBox = widthBox/1.3f
        val isBoxInfoOutsideScreen = (coordinatesInput.x + neededSpaceBox) > screenWidth

        val startXBoxCoordinate = if (isBoxInfoOutsideScreen) screenWidth - neededSpaceBox
        else coordinatesInput.x

        val linePointerInput = Path().apply {
            reset()
            moveTo(coordinatesInput.x, height)
            lineTo(x = coordinatesInput.x, y = spaceTop)
        }

        drawPath(
            path = linePointerInput,
            color = colorLine,
            style = Stroke(
                width = widthStroke,
                cap = StrokeCap.Square,
            )
        )

        drawCircle(
            color = colorCircle,
            radius = radiusCircle,
            center = Offset(x = coordinatesInput.x, y = coordinatesInput.y),
        )

        drawCircle(
            color = colorLine,
            radius = radiusCircle,
            center = Offset(x = coordinatesInput.x, y = coordinatesInput.y),
            style = Stroke(width = widthStroke)
        )

        drawRoundRect(
            color = colorLine,
            size = Size(width = widthBox, height = heightBox),
            topLeft = Offset(x = (startXBoxCoordinate - (widthBox/2)), y = 0f),
            cornerRadius = CornerRadius(x = cornerRadius)
        )

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                closestPoint.timestamp.toReadableDate(),
                startXBoxCoordinate,
                heightBox/2 + textPaint.textSize/3,
                textPaint,
            )
        }
    }
}

private fun DrawScope.drawYAxisInfo(
    chartInfo: AssetChartInfo,
    yAxisHeight: Float,
    yAxisSpaceTop: Float,
    textPaintY: Paint,
    spaceStart: Float,
    spaceBetweenYAndGraph: Float,
) {
    val infoYAxis = mutableListOf<String>()
    val maxItemsY = minOf((chartInfo.barsInfo.size-1), LIMIT_Y_INFO_CHART)
    val yAxisSpace = (yAxisHeight - yAxisSpaceTop) / maxItemsY
    val priceStep = (chartInfo.upperValue - chartInfo.lowerValue) / maxItemsY.toFloat()

    (0 .. maxItemsY).forEach { i ->
        val info = (chartInfo.lowerValue + priceStep * i).formatToString()
        if (!infoYAxis.contains(info)) {
            infoYAxis.add(info)
            val y = if (priceStep == 0.0) (yAxisHeight - yAxisSpaceTop)/2 else yAxisHeight - yAxisSpace * i
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    info,
                    spaceStart - spaceBetweenYAndGraph,
                    y + textPaintY.textSize/3,
                    textPaintY
                )
            }
        }
    }
}

private fun DrawScope.drawPathChart(
    coordinates: List<CoordinatePointChart>,
    graphColor: Color,
    heightAxisY: Float,
) {
    val strokePath = Path().apply {
        reset()
        moveTo(coordinates.first().coordinates.x, coordinates.first().coordinates.y)
        for (i in coordinates.indices) {
            lineTo(
                coordinates[i].coordinates.x, coordinates[i].coordinates.y,
            )
        }
    }

    drawPath(
        path = strokePath,
        color = graphColor,
        style = Stroke(
            width = 4f,
            cap = StrokeCap.Square,
        )
    )

    drawBackgroundChart(
        strokePath = strokePath,
        coordinates = coordinates,
        graphColor = graphColor,
        heightAxisY = heightAxisY,
    )
}

private fun DrawScope.drawBackgroundChart(
    strokePath: Path,
    coordinates: List<CoordinatePointChart>,
    graphColor: Color,
    heightAxisY: Float,
) {
    val fillPath = strokePath
        .apply {
            lineTo(coordinates.last().coordinates.x, heightAxisY)
            lineTo(coordinates.first().coordinates.x, heightAxisY)
            close()
        }
    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                graphColor,
                graphColor.copy(alpha = 0.8f),
                Color.Transparent,
            ),
            endY = heightAxisY,
        ),
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
fun AssetCharPreview() {
    AssetChart(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.horizontalPadding, vertical = 20.dp)
            .height(380.dp),
        chartInfo = AssetChartInfo(),//mockChartData(),
        isLoading = false,
        errorMessage = null,
        colorText = MaterialTheme.colorScheme.onBackground,
        infoToShow = { _, _ ->

        },
        onRetry = {},
    )
}