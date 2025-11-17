package com.example.demo.domain.user

// "창고 관리자는 이런 '버튼'들을 갖고 있습니다" (인터페이스)
interface UserRepository : JpaRepository<User, Long> {

        // '이메일'로 유저 찾기 버튼
        fun findByEmail(email: String): User?

        // '이메일'이 존재하는지 확인하는 버튼
        fun existsByEmail(email: String): Boolean

        // (내가 수정을 제안했던) '로그인 ID'로 유저 찾기 버튼
        fun findByLoginId(loginId: String): User?
}