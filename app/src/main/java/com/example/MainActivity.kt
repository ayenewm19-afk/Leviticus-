package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import com.example.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            QuizDatabase::class.java,
            "quiz_database"
        ).fallbackToDestructiveMigration().build()
    }

    private val repository by lazy {
        QuizRepository(db.quizAttemptDao())
    }

    private val viewModel: QuizViewModel by viewModels {
        QuizViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuizAppContainer(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun QuizAppContainer(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.quizScreen.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (currentScreen) {
            QuizScreen.Welcome -> WelcomeScreen(
                onStartQuiz = { viewModel.startNewQuiz() },
                onViewHistory = { viewModel.setScreen(QuizScreen.History) },
                viewModel = viewModel
            )
            QuizScreen.ActiveQuiz -> ActiveQuizScreen(viewModel = viewModel)
            QuizScreen.Results -> ResultsScreen(
                viewModel = viewModel,
                onRetry = { viewModel.startNewQuiz() },
                onBackHome = { viewModel.setScreen(QuizScreen.Welcome) },
                onViewHistory = { viewModel.setScreen(QuizScreen.History) }
            )
            QuizScreen.History -> HistoryScreen(
                viewModel = viewModel,
                onBackHome = { viewModel.setScreen(QuizScreen.Welcome) }
            )
        }
    }
}

@Composable
fun WelcomeScreen(
    onStartQuiz: () -> Unit,
    onViewHistory: () -> Unit,
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val history by viewModel.attemptsHistory.collectAsStateWithLifecycle()
    val highAttempt = history.maxByOrNull { it.score }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "በስመ አብ ወወልድ ወመንፈስ ቅዱስ አሃዱ አምላክ አሜን",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "የኦሪት ዘሌዋውያን ጥያቄና መልስ ውድድር",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "የኦሪት ዘሌዋውያን ምዕራፍ 1-27",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Beautiful Generated Banner Image Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_bible_scroll_1783594394560),
                        contentDescription = "Bible Scroll Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay to make text clear and add visual depth
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "9ኛ ዙር የመጽሐፍ ቅዱስ ጥያቄዎች (50%)",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "እግዚአብሔር ለሙሴ ከመገናኛው ድንኳን ውስጥ የሰጠው ትእዛዛትና ሥርዓቶች",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // High Score / Progress Status Card
        if (highAttempt != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "High Score Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1.0f)) {
                            Text(
                                text = "የበላይ ከፍተኛ ውጤት (High Score)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${highAttempt.score} ከ ${highAttempt.total} ጥያቄዎች (${(highAttempt.score * 100) / highAttempt.total}%)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Introduction / Instructions Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = borderBrush(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ስለ ውድድሩ መመሪያ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "• ይህ ውድድር በኦሪት ዘሌዋውያን ምዕራፍ 1 እስከ 27 ያሉትን ዋና ዋና መሥዋዕቶች፥ የክህነት ሥርዓቶችን፥ የንጽሕናና የቅድስና ሕጎችን፥ ሰንበታትንና በዓላትን በጥልቀት የሚፈትሹ 50 ጥያቄዎችን የያዘ ነው።\n" +
                               "• እያንዳንዱ ጥያቄ 4 አማራጮች አሉት።\n" +
                               "• የመረጡትን መልስ ካረጋገጡ በኋላ እያንዳንዱ ጥቅስና ምዕራፍ በጥልቀት በቪዲዮ መልክ ወይም በጽሑፍ የተብራራበትን ዝርዝር መረጃ ያገኛሉ።\n" +
                               "• የዕውቀት ደረጃዎን ለመለካት በቂ ዝግጅት አድርገው ይወዳደሩ!",
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Quick Stats Summary Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "ጥያቄዎች ብዛት",
                    value = "50",
                    modifier = Modifier.weight(1.0f)
                )
                StatCard(
                    title = "የማለፊያ ነጥብ",
                    value = "50%",
                    modifier = Modifier.weight(1.0f)
                )
                StatCard(
                    title = "ቋንቋ",
                    value = "አማርኛ",
                    modifier = Modifier.weight(1.0f)
                )
            }
        }

        // Action Buttons Row
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onStartQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_quiz_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ውድድሩን ጀምር (Start)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onViewHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("history_button"),
                shape = RoundedCornerShape(12.dp),
                border = borderBrush()
            ) {
                Icon(imageVector = Icons.Default.History, contentDescription = "History Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ያለፉ ውጤቶች ታሪክ (History)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = borderBrush(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ActiveQuizScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val currentIdx by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val selectedOption by viewModel.selectedOption.collectAsStateWithLifecycle()
    val isAnswered by viewModel.isCurrentAnswered.collectAsStateWithLifecycle()
    val score by viewModel.score.collectAsStateWithLifecycle()

    val questions = viewModel.questions
    val question = questions[currentIdx]

    var showExitDialog by remember { mutableStateOf(false) }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.primary) },
            title = { Text(text = "ውድድሩን ማቋረጥ ይፈልጋሉ?") },
            text = { Text(text = "አሁን ውድድሩን ካቋረጡ የሠሩት ውጤት ሳይቀመጥ ይረሳል። እርግጠኛ ነዎት ማቆም ይፈልጋሉ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        viewModel.setScreen(QuizScreen.Welcome)
                    },
                    modifier = Modifier.testTag("exit_quiz_dialog_confirm")
                ) {
                    Text("አዎ አቁም", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("ይቅርብኝ", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Quiz",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "ጥያቄ ${currentIdx + 1} ከ ${questions.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "ውጤት: $score",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (currentIdx + 1).toFloat() / questions.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                val buttonText = when {
                    !isAnswered -> "መልስ አረጋግጥ (Confirm)"
                    currentIdx < questions.size - 1 -> "ቀጣይ ጥያቄ (Next)"
                    else -> "ውጤቱን እይ (See Results)"
                }

                Button(
                    onClick = {
                        if (!isAnswered) {
                            viewModel.confirmAnswer()
                        } else {
                            viewModel.nextQuestion()
                        }
                    },
                    enabled = selectedOption != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag(if (!isAnswered) "confirm_answer_button" else "next_question_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = buttonText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    if (isAnswered) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Icon"
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Question Statement Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    ),
                    border = borderBrush(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = question.questionText,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 25.sp
                        )
                    }
                }
            }

            // Options list
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OptionRow(
                        optionLetter = 'A',
                        text = question.optionA,
                        isSelected = selectedOption == 'A',
                        isAnswered = isAnswered,
                        isCorrectOption = question.correctAnswer == 'A',
                        onClick = { viewModel.selectOption('A') },
                        modifier = Modifier.testTag("option_a")
                    )
                    OptionRow(
                        optionLetter = 'B',
                        text = question.optionB,
                        isSelected = selectedOption == 'B',
                        isAnswered = isAnswered,
                        isCorrectOption = question.correctAnswer == 'B',
                        onClick = { viewModel.selectOption('B') },
                        modifier = Modifier.testTag("option_b")
                    )
                    OptionRow(
                        optionLetter = 'C',
                        text = question.optionC,
                        isSelected = selectedOption == 'C',
                        isAnswered = isAnswered,
                        isCorrectOption = question.correctAnswer == 'C',
                        onClick = { viewModel.selectOption('C') },
                        modifier = Modifier.testTag("option_c")
                    )
                    OptionRow(
                        optionLetter = 'D',
                        text = question.optionD,
                        isSelected = selectedOption == 'D',
                        isAnswered = isAnswered,
                        isCorrectOption = question.correctAnswer == 'D',
                        onClick = { viewModel.selectOption('D') },
                        modifier = Modifier.testTag("option_d")
                    )
                }
            }

            // Explanation Section
            item {
                AnimatedVisibility(
                    visible = isAnswered,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 40 }),
                    exit = fadeOut()
                ) {
                    val isCorrect = selectedOption == question.correctAnswer
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Clear,
                                    contentDescription = if (isCorrect) "Correct" else "Incorrect",
                                    tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isCorrect) "ትክክለኛ መልስ ነው!" else "መልሱ የተሳሳተ ነው!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                            Text(
                                text = "ትክክለኛው መልስ፡ ${getEthiopianLetter(question.correctAnswer)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = question.explanation,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun OptionRow(
    optionLetter: Char,
    text: String,
    isSelected: Boolean,
    isAnswered: Boolean,
    isCorrectOption: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isAnswered && isCorrectOption -> Color(0xFFE8F5E9) // Success green tint
        isAnswered && isSelected && !isCorrectOption -> Color(0xFFFFEBEE) // Error red tint
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isAnswered && isCorrectOption -> Color(0xFF2E7D32)
        isAnswered && isSelected && !isCorrectOption -> Color(0xFFC62828)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    }

    val textColor = when {
        isAnswered && isCorrectOption -> Color(0xFF1B5E20)
        isAnswered && isSelected && !isCorrectOption -> Color(0xFFB71C1C)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    val textWeight = if (isSelected || (isAnswered && isCorrectOption)) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !isAnswered) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = textWeight,
            fontSize = 14.sp,
            modifier = Modifier.weight(1.0f)
        )
        if (isAnswered && isCorrectOption) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Correct Answer Indicator",
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(20.dp)
            )
        } else if (isAnswered && isSelected && !isCorrectOption) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Incorrect Answer Indicator",
                tint = Color(0xFFC62828),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ResultsScreen(
    viewModel: QuizViewModel,
    onRetry: () -> Unit,
    onBackHome: () -> Unit,
    onViewHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val score by viewModel.score.collectAsStateWithLifecycle()
    val questions = viewModel.questions
    val percentage = (score * 100) / questions.size

    val (ratingText, ratingColor) = when {
        percentage >= 90 -> "ብቁና እጅግ ምርጥ!" to MaterialTheme.colorScheme.primary
        percentage >= 70 -> "በጣም ጥሩ!" to Color(0xFF2E7D32)
        percentage >= 50 -> "ጥሩ ዝግጅት!" to Color(0xFFE65100)
        else -> "የበለጠ ጥረትና ጥናት ያስፈልጋል!" to Color(0xFFC62828)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Star Cup decoration icon
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star Decoration",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "እንኳን ደስ አላችሁ!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "ውድድሩን በስኬት አጠናቀዋል",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(28.dp))

        // Large circular score meter
        Box(
            modifier = Modifier
                .size(160.dp)
                .border(6.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "ከ ${questions.size}",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$percentage% ተጠናቋል",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = ratingText,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ratingColor,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons Grid-Column
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("restart_quiz_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "ውድድሩን እንደገና ጀምር", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onViewHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                border = borderBrush(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.History, contentDescription = "History")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ውጤቶች ማህደር (Past History)",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = onBackHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("back_to_welcome_button")
            ) {
                Text(
                    text = "ወደ መግቢያ ገጽ ተመለስ (Home)",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HistoryScreen(
    viewModel: QuizViewModel,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val history by viewModel.attemptsHistory.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon", tint = MaterialTheme.colorScheme.primary) },
            title = { Text(text = "ውጤቶችን ለማጥፋት እርግጠኛ ነዎት?") },
            text = { Text(text = "የተመዘገቡት የውድድር ታሪኮች ሁሉ በቋሚነት ይጠፋሉ። ይህ ድርጊት ሊመለስ አይችልም።") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.clearHistory()
                    },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Text("ሁሉንም አጥፋ", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("ይቅርብኝ", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackHome) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Back Home",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "ያለፉ ውጤቶች ታሪክ (History)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1.0f)
                )
                if (history.isNotEmpty()) {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear History",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (history.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "No History",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "የተመዘገበ የታሪክ ውጤት የለም!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ውድድሩን መሳተፍ ሲጀምሩ ውጤትዎ እዚህ ይቀመጣል።",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.startNewQuiz() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ውድድሩን አሁን ጀምር")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Average Stats Banner
                    val totalAttempts = history.size
                    val avgScore = history.map { it.score }.average()
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                        ),
                        border = borderBrush(alpha = 0.3f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ጠቅላላ ተሳትፎ",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "$totalAttempts ጊዜ",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column {
                                Text(
                                    text = "አማካይ ውጤት",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f", avgScore) + " ከ 50",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column {
                                val highPercent = (history.maxOf { it.score } * 100) / 50
                                Text(
                                    text = "ከፍተኛ ውጤት",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "$highPercent%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }

                items(history) { attempt ->
                    HistoryItemRow(attempt = attempt)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun HistoryItemRow(
    attempt: QuizAttempt,
    modifier: Modifier = Modifier
) {
    val percent = (attempt.score * 100) / attempt.total
    val (ratingText, ratingColor) = when {
        percent >= 90 -> "እጅግ ምርጥ!" to MaterialTheme.colorScheme.primary
        percent >= 70 -> "በጣም ጥሩ!" to Color(0xFF2E7D32)
        percent >= 50 -> "ጥሩ!" to Color(0xFFE65100)
        else -> "ድጋሚ ፈትሽ!" to Color(0xFFC62828)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = borderBrush(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1.0f)) {
                Text(
                    text = "በ ${formatTimestamp(attempt.timestamp)}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${attempt.score} ከ ${attempt.total} በትክክል ተመልሷል",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$percent%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = ratingColor
                )
                Text(
                    text = ratingText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ratingColor
                )
            }
        }
    }
}

// Helpers
fun getEthiopianLetter(correctAnswer: Char): String {
    return when (correctAnswer) {
        'A' -> "ሀ"
        'B' -> "ለ"
        'C' -> "ሐ"
        'D' -> "መ"
        else -> ""
    }
}

fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM d, yyyy - hh:mm a", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "ያልታወቀ ቀን"
    }
}

@Composable
fun borderBrush(alpha: Float = 0.5f): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
    )
}
