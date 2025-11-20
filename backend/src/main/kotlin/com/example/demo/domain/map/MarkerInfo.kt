package com.example.demo.domain.map.dto

import com.example.demo.domain.place.model.PlaceCategory

data class MarkerInfo(
    val placeId: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,

    // 정보 카드용 데이터
    val category: PlaceCategory,
    val rating: Double,
    val reviewCount: Int,         // 인기순 정렬 및 표시용
    val thumbnail: String?,       // 대표 사진 1장 URL
    val petPolicy: String,        // "대형견 가능" 등 텍스트 정책
    val isOffLeash: Boolean,      // 오프리쉬 가능여부
    val hasParking: Boolean,

    val distance: Double? = null  // 내 위치로부터의 거리 (m)
)