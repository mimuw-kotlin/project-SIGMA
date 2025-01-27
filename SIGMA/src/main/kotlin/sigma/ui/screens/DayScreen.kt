package sigma.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import sigma.businessLogic.impl.managers.ResolutionsManager
import sigma.dataAccess.impl.data.CompletionStatus
import java.time.LocalDate

class DayScreen(
    private val manager: ResolutionsManager,
    private val date: LocalDate
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Row: Navigation and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = date.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h5
                )
                Row {
                    IconButton(onClick = {
                        navigator.replace(DayScreen(manager, date.minusDays(1)))
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Day"
                        )
                    }
                    IconButton(onClick = {
                        navigator.replace(DayScreen(manager, date.plusDays(1)))
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Day"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pie Chart and Score
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pie Chart
                PieChart(
                    manager.getCountOf(CompletionStatus.UNKNOWN, date),
                    manager.getCountOf(CompletionStatus.COMPLETED, date),
                    manager.getCountOf(CompletionStatus.PARTIAL, date),
                    manager.getCountOf(CompletionStatus.UNCOMPLETED, date),
                )

                // Score
                Text(
                    text = String.format("%.2f%%", manager.getScore(date) * 100),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resolutions Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                val state = rememberLazyListState()

                LazyRow(Modifier, state) {
                    items(manager.getResolutions().size) { index ->
                        ResolutionBox(
                            resolutionName = manager.getResolutions()[index].name,
                            completionStatus = manager.getCompletionStatus(date, index),
                            color = manager.getColorOfCompletionStatus(
                                manager.getCompletionStatus(date, index)
                            )
                        )
                    }
                }
                HorizontalScrollbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    ),
                    style = ScrollbarStyle(
                        hoverColor = MaterialTheme.colors.primary,
                        unhoverColor = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                        minimalHeight = 16.dp,
                        thickness = 8.dp,
                        shape = MaterialTheme.shapes.small,
                        hoverDurationMillis = 100
                    )
                )

            }
        }
    }

    @Composable
    private fun PieChart(unknown: Int, completed: Int, partial: Int, uncompleted: Int) {
        val total = unknown + completed + partial + uncompleted
        val proportions = listOf(
            unknown.toFloat() / total,
            completed.toFloat() / total,
            partial.toFloat() / total,
            uncompleted.toFloat() / total
        )
        val colors = listOf(
            manager.getColorOfCompletionStatus(CompletionStatus.UNKNOWN),
            manager.getColorOfCompletionStatus(CompletionStatus.COMPLETED),
            manager.getColorOfCompletionStatus(CompletionStatus.PARTIAL),
            manager.getColorOfCompletionStatus(CompletionStatus.UNCOMPLETED)
        )

        Canvas(modifier = Modifier.size(200.dp)) {
            var startAngle = 0f
            proportions.forEachIndexed { index, proportion ->
                val sweepAngle = proportion * 360
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }
    }

    @Composable
    private fun ResolutionBox(resolutionName: String, completionStatus: CompletionStatus, color: Color) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .size(150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = resolutionName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Status Box
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (completionStatus) {
                        CompletionStatus.UNKNOWN -> "?"
                        CompletionStatus.COMPLETED -> "✔"
                        CompletionStatus.PARTIAL -> "±"
                        CompletionStatus.UNCOMPLETED -> "✘"
                    },
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
