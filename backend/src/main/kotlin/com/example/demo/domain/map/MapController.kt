package com.example.demo.domain.map

import com.example.demo.domain.map.dto.MapSearchCondition
import com.example.demo.domain.map.dto.MapSearchResponse
import com.example.demo.domain.user.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/map")
class MapController(
    private val mapService: MapService
) {

    // 지도 검색 (POST로 요청해야 다양한 필터 조건을 JSON으로 보내기 편함)
    @PostMapping("/search")
    fun searchMap(@Valid @RequestBody condition: MapSearchCondition): ResponseEntity<ApiResponse<MapSearchResponse>> {
        val response = mapService.searchInArea(condition)
        return ResponseEntity.ok(
            ApiResponse(success = true, message = "지도 검색 성공", data = response)
        )
    }
}