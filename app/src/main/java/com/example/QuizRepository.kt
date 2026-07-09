package com.example

import kotlinx.coroutines.flow.Flow

class QuizRepository(private val dao: QuizAttemptDao) {
    val allAttempts: Flow<List<QuizAttempt>> = dao.getAllAttempts()

    suspend fun insertAttempt(attempt: QuizAttempt) {
        dao.insertAttempt(attempt)
    }

    suspend fun clearAttempts() {
        dao.clearAllAttempts()
    }
}
