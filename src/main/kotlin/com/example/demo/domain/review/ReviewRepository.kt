package com.example.demo.domain.review

import org.springframework.data.jpa.repository.JpaRepository


interface ReviewRepository : JpaRepository<Review, Long> {

    // 1. 특정 장소(Place)에 달린 모든 리뷰 조회 (최신순 정렬 권장)
    fun findAllByPlaceIdOrderByCreatedAtDesc(placeId: Long): List<Review>

    // 2. 특정 유저(User)가 쓴 모든 리뷰 조회 (마이페이지용)
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<Review>
}