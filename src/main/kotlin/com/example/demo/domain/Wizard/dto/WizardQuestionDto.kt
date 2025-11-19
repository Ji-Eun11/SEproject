package com.example.demo.domain.wizard.dto

data class WizardQuestionDto(
    val questionId: Long,
    val step: Int,
    val questionText: String,
    val answers: List<WizardAnswerDto>
)
