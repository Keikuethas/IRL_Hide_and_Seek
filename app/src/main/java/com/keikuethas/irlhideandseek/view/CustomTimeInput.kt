package com.keikuethas.irlhideandseek.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CustomTimeInputDialog(
    initTime: Int,
    text: String,
    onPick: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val initMin = (initTime / 60).coerceIn(0, 99)
    val initSec = (initTime % 60).coerceIn(0, 59)

    var selectedMinutes by remember { mutableStateOf(initMin) }
    var selectedSeconds by remember { mutableStateOf(initSec) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(24.dp))

                // Структура с жёсткой фиксацией высоты для идеального выравнивания
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Левая колонка
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        WheelPickerColumn(
                            range = 0..99,
                            initialValue = initMin,
                            onValueChange = { selectedMinutes = it },
                            modifier = Modifier.width(80.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Мин",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Двоеточие (строго по центру высоты пикера)
                    Box(
                        modifier = Modifier.height(200.dp), // 5 * 40.dp
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ":",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Правая колонка
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        WheelPickerColumn(
                            range = 0..59,
                            initialValue = initSec,
                            onValueChange = { selectedSeconds = it },
                            modifier = Modifier.width(80.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Сек",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) { Text("Отмена", maxLines = 1) }

                    Button(
                        onClick = { onPick(selectedMinutes * 60 + selectedSeconds) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        enabled = selectedMinutes + selectedSeconds > 0,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) { Text("Применить", maxLines = 1) }
                }
            }
        }
    }
}

@Composable
private fun WheelPickerColumn(
    range: IntRange,
    initialValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 40.dp
    val visibleItemsCount = 5
    val paddingItems = visibleItemsCount / 2

    val listState = rememberLazyListState()

    // Инициализация: скроллим к индексу, padding автоматически центрирует элемент
    LaunchedEffect(initialValue) {
        val targetIndex = (initialValue - range.first).coerceIn(0, range.last - range.first)
        listState.scrollToItem(targetIndex)
    }

    // Выбор элемента: при snapped-скролле первый видимый элемент всегда центральный
    val selectedValue by remember {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex
            (range.first + index).coerceIn(range)
        }
    }

    LaunchedEffect(selectedValue, listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            onValueChange(selectedValue)
        }
    }

    Box(
        modifier = modifier.height(itemHeight * visibleItemsCount)
    ) {
        // Фоновая подсветка строго по центру Box
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
        )

        LazyColumn(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = itemHeight * paddingItems)
        ) {
            items(range.count()) { index ->
                val value = range.first + index
                val isSelected = value == selectedValue

                Text(
                    text = value.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    ),
                    // fillMaxWidth() + TextAlign.Center гарантируют строгое центрирование текста
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}