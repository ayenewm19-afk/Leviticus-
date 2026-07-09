package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class QuizScreen {
    Welcome,
    ActiveQuiz,
    Results,
    History
}

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

    val questions = QuizQuestion.allQuestions

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex

    private val _selectedOption = MutableStateFlow<Char?>(null)
    val selectedOption: StateFlow<Char?> = _selectedOption

    private val _isCurrentAnswered = MutableStateFlow(false)
    val isCurrentAnswered: StateFlow<Boolean> = _isCurrentAnswered

    // User's answers for each question index (size is 50)
    private val _userAnswers = MutableStateFlow<List<Char?>>(List(questions.size) { null })
    val userAnswers: StateFlow<List<Char?>> = _userAnswers

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _quizScreen = MutableStateFlow(QuizScreen.Welcome)
    val quizScreen: StateFlow<QuizScreen> = _quizScreen

    val attemptsHistory: StateFlow<List<QuizAttempt>> = repository.allAttempts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectOption(option: Char) {
        if (!_isCurrentAnswered.value) {
            _selectedOption.value = option
        }
    }

    fun confirmAnswer() {
        val currentIdx = _currentQuestionIndex.value
        val currentOption = _selectedOption.value
        if (currentOption != null && !_isCurrentAnswered.value) {
            _isCurrentAnswered.value = true
            
            // Save answer
            val currentAnswers = _userAnswers.value.toMutableList()
            currentAnswers[currentIdx] = currentOption
            _userAnswers.value = currentAnswers

            // Verify correctness
            val question = questions[currentIdx]
            if (currentOption == question.correctAnswer) {
                _score.value += 1
            }
        }
    }

    fun nextQuestion() {
        val currentIdx = _currentQuestionIndex.value
        if (currentIdx < questions.size - 1) {
            _currentQuestionIndex.value = currentIdx + 1
            _selectedOption.value = null
            _isCurrentAnswered.value = false
        } else {
            // End of quiz - Save to DB
            saveQuizResult()
            _quizScreen.value = QuizScreen.Results
        }
    }

    private fun saveQuizResult() {
        viewModelScope.launch {
            val attempt = QuizAttempt(
                score = _score.value,
                total = questions.size
            )
            repository.insertAttempt(attempt)
        }
    }

    fun startNewQuiz() {
        _currentQuestionIndex.value = 0
        _selectedOption.value = null
        _isCurrentAnswered.value = false
        _userAnswers.value = List(questions.size) { null }
        _score.value = 0
        _quizScreen.value = QuizScreen.ActiveQuiz
    }

    fun setScreen(screen: QuizScreen) {
        _quizScreen.value = screen
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAttempts()
        }
    }
}

class QuizViewModelFactory(private val repository: QuizRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
