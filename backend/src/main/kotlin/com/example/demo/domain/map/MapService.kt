package com.example.demo.domain.map

import com.example.demo.domain.map.dto.MapSearchCondition
import com.example.demo.domain.map.dto.MapSearchResponse
import com.example.demo.domain.map.mapper.PlaceMapper
import com.example.demo.domain.place.PlaceRepository
import com.example.demo.domain.place.model.LocationType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MapService(
    private val placeRepository: PlaceRepository,
    private val locationService: LocationService
) {

    fun searchInArea(condition: MapSearchCondition): MapSearchResponse {
        // DB 조회 (좌표 범위)
        val places = placeRepository.findPlacesInArea(
            minLat = condition.minLatitude,
            maxLat = condition.maxLatitude,
            minLon = condition.minLongitude,
            maxLon = condition.maxLongitude
        )

        // 메모리 필터링
        val filteredPlaces = places.filter { place ->
            val matchKeyword = condition.keyword.isNullOrBlank() ||
                    place.name.contains(condition.keyword, ignoreCase = true) ||
                    place.address.contains(condition.keyword, ignoreCase = true)

            val matchCategory = condition.category == null || place.category == condition.category

            val matchParking = condition.hasParking == null || condition.hasParking == false ||
                    place.hasParking == condition.hasParking

            val matchOffLeash = condition.isOffLeash == null || condition.isOffLeash == false ||
                    place.isOffLeash == condition.isOffLeash

            val matchLocation = condition.locationType == null ||
                    place.locationType == LocationType.BOTH ||
                    place.locationType == condition.locationType

            val matchSize = condition.dogSize == null ||
                    place.allowedSizes.contains(condition.dogSize)

            val matchRating = condition.minRating == null || place.avgRating >= condition.minRating

            matchKeyword && matchCategory && matchParking &&
                    matchOffLeash && matchLocation && matchSize && matchRating
        }

        // 거리 계산 수행
        val userLoc = locationService.getCurrentLocation()
        var markers = filteredPlaces.map { place ->
            PlaceMapper.toMarkerInfo(place, userLoc)
        }

        // 4. 정렬 (이미 계산된 DTO의 필드를 기준으로 정렬)
        markers = when (condition.sort) {
            MapSortType.RATING -> markers.sortedByDescending { it.rating }
            MapSortType.POPULARITY -> markers.sortedByDescending { it.reviewCount }
            MapSortType.DISTANCE -> markers.sortedBy { it.distance ?: Double.MAX_VALUE } // 거리가 없으면 맨 뒤로
        }

        return MapSearchResponse(
            totalCount = markers.size,
            markers = markers
        )
    }
}