package com.example.demo.domain.wizard.dto

import jakarta.validation.constraints.NotNull

// 마법사 질문 조회 응답 DTO
data class WizardQuestionDto(
    val questionId: Long,
    val step: Int,
    val questionText: String,
    val answers: List<WizardAnswerDto>
)

// 마법사 답변 정보 DTO
data class WizardAnswerDto(
    val answerId: Long,
    val answerText: String,
    val matchingTag: String // Enum name 전달
)

// 마법사 추천 요청 DTO
data class WizardRecommendRequest(
    @field:NotNull val selectedAnswerIds: List<Long>,

    // 사용자 위치 (거리 계산용)
    val userLatitude: Double? = null,
    val userLongitude: Double? = null
)