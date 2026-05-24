package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val voices by viewModel.russianVoiceNames.collectAsStateWithLifecycle()
    val ttsReady by viewModel.ttsReady.collectAsStateWithLifecycle()

    var customValueInput by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepIndigoBackground,
                        NavyCharcoal,
                        DeepIndigoBackground
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Счётчик и Голос",
                            color = CreamWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. MAIN DISPLAY CARD
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassyCardBg),
                    border = BorderStroke(1.dp, SoftSlateBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            // Subtly draw glowing background in the center
                            drawCircle(
                                color = GlowAmber,
                                radius = size.minDimension / 2.5f,
                                alpha = 0.3f
                            )
                        }
                        .testTag("counter_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ТЕКУЩИЙ СЧЁТ",
                            style = MaterialTheme.typography.labelMedium,
                            color = SoftGrayText,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large beautiful Counter text matching the original counter
                        AnimatedContent(
                            targetState = settings.count,
                            transitionSpec = {
                                (slideInVertically { height -> height } + fadeIn() togetherWith
                                        slideOutVertically { height -> -height } + fadeOut())
                                    .using(SizeTransform(clip = false))
                            },
                            label = "CounterAnimation"
                        ) { targetCount ->
                            Text(
                                text = targetCount.toString(),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 76.sp,
                                    fontWeight = FontWeight.Black,
                                    color = WarmAmberGold
                                ),
                                modifier = Modifier.testTag("counter_value")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "страница",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftGrayText
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Increment directly in card button
                        Button(
                            onClick = { viewModel.increment() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WarmAmberGold,
                                contentColor = DeepIndigoBackground
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("increment_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Увеличить",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Увеличить на 1",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // 2. SET CUSTOM VALUE CARD
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassyCardBg),
                    border = BorderStroke(1.dp, SoftSlateBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "ЗАДАТЬ ЗНАЧЕНИЕ",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftGrayText,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = customValueInput,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() }) {
                                        customValueInput = input
                                    }
                                },
                                placeholder = {
                                    Text(
                                        text = "Введите число",
                                        color = SoftGrayText.copy(alpha = 0.6f)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WarmAmberGold,
                                    unfocusedBorderColor = SoftSlateBlue,
                                    focusedTextColor = CreamWhite,
                                    unfocusedTextColor = CreamWhite,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("custom_value_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    val valInt = customValueInput.toIntOrNull()
                                    if (valInt != null) {
                                        viewModel.setCustomValue(valInt)
                                        customValueInput = ""
                                        keyboardController?.hide()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Пожалуйста, введите корректное число.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SoftSlateBlue,
                                    contentColor = CreamWhite
                                ),
                                border = BorderStroke(1.dp, DarkGoldAccent.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(54.dp)
                                    .testTag("set_custom_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Задать"
                                )
                            }
                        }
                    }
                }

                // 3. VOICE ACTION PANEL (Page actions)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassyCardBg),
                    border = BorderStroke(1.dp, SoftSlateBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "ОЗВУЧКА И НАВИГАЦИЯ",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftGrayText,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        // Button Speak page
                        Button(
                            onClick = { viewModel.speak() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftSlateBlue,
                                contentColor = CreamWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                            enabled = ttsReady,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("speak_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Произнести",
                                tint = WarmAmberGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Произнести",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Button Next page & Speak
                        Button(
                            onClick = { viewModel.incrementAndSpeak() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftSlateBlue,
                                contentColor = CreamWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                            enabled = ttsReady,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("next_and_speak_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Следующая страница",
                                tint = WarmAmberGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Следующая страница",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // 4. DEVOTIONAL ACTIONS PANEL (Amida & Tzitzit)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassyCardBg),
                    border = BorderStroke(1.dp, SoftSlateBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "ПОДГОТОВИТЕЛЬНЫЕ МОЛИТВЫ",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftGrayText,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        // Button Tzitzit
                        Button(
                            onClick = { viewModel.speakTzitzit() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftSlateBlue,
                                contentColor = CreamWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                            enabled = ttsReady,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("speak_tzitzit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Цицит",
                                tint = WarmAmberGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Собираем кисти цицит",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Button Amida
                        Button(
                            onClick = { viewModel.speakAmida() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftSlateBlue,
                                contentColor = CreamWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                            enabled = ttsReady,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("speak_amida_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Амида",
                                tint = WarmAmberGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Готовимся к молитве Амида",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // 5. VOICE SELECTOR PANEL
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassyCardBg),
                    border = BorderStroke(1.dp, SoftSlateBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ВЫБОР ГОЛОСА ОЗВУЧКИ",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftGrayText,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SoftSlateBlue)
                                .clickable { if (ttsReady) dropdownExpanded = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                .testTag("voice_dropdown")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val selectedVoice = settings.selectedVoiceName.ifEmpty {
                                    if (voices.isNotEmpty()) voices.first() else "Системный по умолчанию (Русский)"
                                }

                                Text(
                                    text = selectedVoice,
                                    color = CreamWhite,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                Icon(
                                    imageVector = if (dropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Выбор голоса",
                                    tint = WarmAmberGold
                                )
                            }

                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .background(NavyCharcoal)
                                    .border(1.dp, SoftSlateBlue)
                            ) {
                                if (voices.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("Системный по умолчанию (Русский)", color = CreamWhite) },
                                        onClick = {
                                            viewModel.selectVoice("")
                                            dropdownExpanded = false
                                        }
                                    )
                                } else {
                                    voices.forEach { voiceName ->
                                        DropdownMenuItem(
                                            text = { Text(voiceName, color = CreamWhite) },
                                            onClick = {
                                                viewModel.selectVoice(voiceName)
                                                dropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        if (!ttsReady) {
                            Text(
                                text = "Инициализация синтезатора речи...",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmAmberGold.copy(alpha = 0.8f),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
