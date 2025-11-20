package com.example.demo.domain.place

import com.example.demo.domain.place.model.DogSize
import com.example.demo.domain.place.model.LocationType
import com.example.demo.domain.place.model.PlaceCategory
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "places")
class Place(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var address: String,

    var phone: String? = null,
    var operationHours: String? = null,

    // [필터용 필드들]
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var category: PlaceCategory,

    var hasParking: Boolean = false,
    var isOffLeash: Boolean = false,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var locationType: LocationType,

    // "소형견, 대형견" 다 되는 곳이 있으므로 Set으로 관리
    @ElementCollection(targetClass = DogSize::class)
    @CollectionTable(name = "place_allowed_sizes", joinColumns = [JoinColumn(name = "place_id")])
    @Enumerated(EnumType.STRING)
    var allowedSizes: MutableSet<DogSize> = mutableSetOf(),

    @Column(nullable = false)
    var petPolicy: String, // 텍스트형 상세 정책 (예: "입질있는 강아지 불가")

    var latitude: Double? = null,
    var longitude: Double? = null

) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val placeId: Long = 0

    @Column(nullable = false)
    var avgRating: Double = 0.0

    @Column(nullable = false)
    var reviewCount: Int = 0

    @ElementCollection
    @CollectionTable(name = "place_photos", joinColumns = [JoinColumn(name = "place_id")])
    @Column(name = "photo_url")
    var photos: MutableList<String> = mutableListOf()

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set

    // 정보 수정 편의 메서드
    fun updateInfo(
        name: String, address: String, phone: String?, operationHours: String?,
        petPolicy: String, category: PlaceCategory, locationType: LocationType,
        hasParking: Boolean, isOffLeash: Boolean, allowedSizes: Set<DogSize>,
        latitude: Double?, longitude: Double?, newPhotos: List<String>
    ) {
        this.name = name
        this.address = address
        this.phone = phone
        this.operationHours = operationHours
        this.petPolicy = petPolicy
        this.category = category
        this.locationType = locationType
        this.hasParking = hasParking
        this.isOffLeash = isOffLeash
        this.allowedSizes.clear()
        this.allowedSizes.addAll(allowedSizes)
        this.latitude = latitude
        this.longitude = longitude
        this.photos.clear()
        this.photos.addAll(newPhotos)
        this.updatedAt = LocalDateTime.now()
    }

    // 평점/리뷰수 갱신
    fun updateRatingInfo(newRating: Double, newReviewCount: Int) {
        this.avgRating = newRating
        this.reviewCount = newReviewCount
        this.updatedAt = LocalDateTime.now()
    }
}