package com.example.demo.domain.map.mapper

import com.example.demo.domain.map.DistanceCalculator
import com.example.demo.domain.map.Location
import com.example.demo.domain.map.dto.MarkerInfo
import com.example.demo.domain.place.Place

object PlaceMapper {

    fun toMarkerInfo(
        place: Place,
        userLocation: Location?
    ): MarkerInfo {

        // 거리 계산 (사용자 위치가 있을 때만)
        val distance = userLocation?.let {
            DistanceCalculator.calculate(
                lat1 = it.latitude,
                lon1 = it.longitude,
                lat2 = place.latitude ?: 0.0,
                lon2 = place.longitude ?: 0.0
            )
        }

        // 엔티티 -> DTO 변환
        return MarkerInfo(
            placeId = place.placeId,
            name = place.name,
            latitude = place.latitude ?: 0.0,
            longitude = place.longitude ?: 0.0,

            // [새로운 필드 매핑]
            category = place.category,
            rating = place.avgRating,
            reviewCount = place.reviewCount,
            thumbnail = place.photos.firstOrNull(), // 첫 번째 사진 사용
            petPolicy = place.petPolicy,
            isOffLeash = place.isOffLeash,
            hasParking = place.hasParking,

            distance = distance
        )
    }
}