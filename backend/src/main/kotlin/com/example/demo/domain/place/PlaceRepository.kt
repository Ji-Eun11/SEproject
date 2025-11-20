package com.example.demo.domain.place

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PlaceRepository : JpaRepository<Place, Long> {

    // 검색 기능: 장소 이름에 특정 단어가 포함된 곳 찾기
    fun findByNameContainingIgnoreCase(keyword: String): List<Place>

    // 지역 검색: 주소에 특정 지역명이 포함된 곳 찾기 (예: "강남구")
    fun findByAddressContainingIgnoreCase(region: String): List<Place>

    // 지도 사각형 영역(MBR) 내의 장소 검색
    @Query("""
        SELECT p FROM Place p 
        WHERE p.latitude BETWEEN :minLat AND :maxLat
        AND p.longitude BETWEEN :minLon AND :maxLon
    """)
    fun findPlacesInArea(
        @Param("minLat") minLat: Double,
        @Param("maxLat") maxLat: Double,
        @Param("minLon") minLon: Double,
        @Param("maxLon") maxLon: Double
    ): List<Place>
}