package com.example.demo.domain.map.dto

import com.example.demo.domain.map.MapSortType
import com.example.demo.domain.place.model.DogSize
import com.example.demo.domain.place.model.LocationType
import com.example.demo.domain.place.model.PlaceCategory
import jakarta.validation.constraints.NotNull

// 요청 DTO (검색 조건)
data class MapSearchCondition(
    @field:NotNull val minLatitude: Double,
    @field:NotNull val maxLatitude: Double,
    @field:NotNull val minLongitude: Double,
    @field:NotNull val maxLongitude: Double,

    val keyword: String? = null,
    val category: PlaceCategory? = null,
    val dogSize: DogSize? = null,
    val hasParking: Boolean? = null,
    val isOffLeash: Boolean? = null,
    val locationType: LocationType? = null,
    val minRating: Double? = null,

    val sort: MapSortType = MapSortType.RATING
)

// 응답 DTO (결과 반환)
data class MapSearchResponse(
    val totalCount: Int,
    val markers: List<MarkerInfo>
)