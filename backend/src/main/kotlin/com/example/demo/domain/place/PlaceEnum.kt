package com.example.demo.domain.place.model

// 장소 카테고리
enum class PlaceCategory(val description: String) {
    CAFE("카페"),
    RESTAURANT("음식점"),
    PLAYGROUND("운동장"),
    SWIMMING("물놀이"),
    ACCOMMODATION("숙소"),
    PARK("공원")
}

// 견종 크기
enum class DogSize(val description: String) {
    SMALL("소형견"),
    MEDIUM("중형견"),
    LARGE("대형견")
}

// 실내/실외 여부
enum class LocationType(val description: String) {
    INDOOR("실내"),
    OUTDOOR("야외"),
    BOTH("실내+야외")
}

enum class WizardTag {
    // Q1. 강아지 크기 (DogSize와 매핑됨)
    SMALL, MEDIUM, LARGE,

    // Q2. 컨디션
    ENERGY_HIGH, ENERGY_LOW,

    // Q3. 이동 거리
    DIST_NEAR, DIST_MID, DIST_FAR,

    // Q4. 장소 유형
    TYPE_NATURE, TYPE_CITY, TYPE_PRIVATE
}