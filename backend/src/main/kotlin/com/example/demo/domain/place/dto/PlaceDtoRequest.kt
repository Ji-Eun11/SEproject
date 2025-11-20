package com.example.demo.domain.place.dto

import com.example.demo.domain.place.model.DogSize
import com.example.demo.domain.place.model.LocationType
import com.example.demo.domain.place.model.PlaceCategory

data class PlaceCreateRequest(
    val name: String,
    val address: String,
    val phone: String? = null,
    val operationHours: String? = null,
    val petPolicy: String,

    val category: PlaceCategory,
    val locationType: LocationType,     // 예: "INDOOR", "OUTDOOR"
    val allowedSizes: Set<DogSize>,     // 예: ["SMALL", "MEDIUM"]
    val hasParking: Boolean = false,
    val isOffLeash: Boolean = false,

    val latitude: Double? = null,
    val longitude: Double? = null,
    val photos: List<String> = emptyList()
)

data class PlaceUpdateRequest(
    val name: String,
    val address: String,
    val phone: String? = null,
    val operationHours: String? = null,
    val petPolicy: String,

    val category: PlaceCategory,
    val locationType: LocationType,
    val allowedSizes: Set<DogSize>,
    val hasParking: Boolean,
    val isOffLeash: Boolean,

    val latitude: Double? = null,
    val longitude: Double? = null,
    val photos: List<String> = emptyList()
)