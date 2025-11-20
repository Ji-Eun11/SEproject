package com.example.demo.domain.wizard

import org.springframework.data.jpa.repository.JpaRepository

// 질문 조회용
interface WizardQuestionRepository : JpaRepository<WizardQuestion, Long> {
    fun findAllByOrderByStepAsc(): List<WizardQuestion>
}

// 답변 조회용
interface WizardAnswerRepository : JpaRepository<WizardAnswer, Long>