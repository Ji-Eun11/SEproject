package com.example.demo.domain.user

import com.example.demo.domain.user.User
import com.example.demo.domain.user.UserRole
import java.time.LocalDateTime

data class UserResponse(
    val userId: Long,
    val loginId: String, // (추가)
    val email: String,
    val nickname: String,
    val profileImage: String?,
    val role: UserRole,
    val pets: List<PetProfileDto>, // (추가)
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                userId = user.userId,
                loginId = user.loginId,
                email = user.email,
                nickname = user.nickname,
                profileImage = user.profileImage,
                role = user.role,
                // (추가) Member Entity의 Pet Entity 목록을 PetProfileDto 목록으로 변환
                pets = user.pets.map { pet ->
                    PetProfileDto(
                        petId = pet.petId,
                        name = pet.name,
                        size = pet.size
                    )
                },
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }
}

/**
 * (추가) MemberResponse에 포함될 Pet 정보 DTO
 */
data class PetProfileDto(
    val petId: Long,
    val name: String,
    val size: String
)

/**
 * (유지) 로그인 성공 응답 DTO
 */
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse // (수정) UserResponse -> MemberResponse
)

/**
 * (유지) 공통 API 응답 래퍼
 * (이 템플릿은 매우 훌륭합니다)
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)