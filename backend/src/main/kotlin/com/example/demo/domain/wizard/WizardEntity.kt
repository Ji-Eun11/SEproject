package com.example.demo.domain.wizard

import com.example.demo.domain.place.model.WizardTag
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "wizard_questions")
@EntityListeners(AuditingEntityListener::class)
class WizardQuestion(
    @Column(nullable = false)
    val step: Int,

    @Column(nullable = false, length = 200)
    val questionText: String,

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val answers: MutableList<WizardAnswer> = mutableListOf()
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val questionId: Long = 0

    @CreatedDate lateinit var createdAt: LocalDateTime
    @LastModifiedDate lateinit var updatedAt: LocalDateTime
}

@Entity
@Table(name = "wizard_answers")
@EntityListeners(AuditingEntityListener::class)
class WizardAnswer(
    @Column(nullable = false, length = 200)
    val answerText: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    val matchingTag: WizardTag, // PlaceEnum.kt에 정의된 Enum 사용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    val question: WizardQuestion
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val answerId: Long = 0

    @CreatedDate lateinit var createdAt: LocalDateTime
    @LastModifiedDate lateinit var updatedAt: LocalDateTime
}