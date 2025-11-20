package com.example.demo.domain.wizard.service

import com.example.demo.domain.map.DistanceCalculator
import com.example.demo.domain.place.Place
import com.example.demo.domain.place.PlaceRepository
import com.example.demo.domain.place.dto.PlaceDtoResponse
import com.example.demo.domain.place.model.DogSize
import com.example.demo.domain.place.model.LocationType
import com.example.demo.domain.place.model.PlaceCategory
import com.example.demo.domain.place.model.WizardTag
import com.example.demo.domain.wizard.dto.WizardAnswerDto
import com.example.demo.domain.wizard.WizardQuestionRepository
import com.example.demo.domain.wizard.WizardAnswerRepository
import com.example.demo.domain.wizard.dto.WizardQuestionDto
import com.example.demo.domain.wizard.dto.WizardRecommendRequest

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WizardService(
    private val questionRepository: WizardQuestionRepository,
    private val answerRepository: WizardAnswerRepository,
    private val placeRepository: PlaceRepository
) {

    // 마법사 질문 전체 리스트 반환
    @Transactional(readOnly = true)
    fun getWizardQuestions(): List<WizardQuestionDto> {
        return questionRepository.findAllByOrderByStepAsc().map { q ->
            WizardQuestionDto(
                questionId = q.questionId,
                step = q.step,
                questionText = q.questionText,
                answers = q.answers.map { a ->
                    WizardAnswerDto(
                        answerId = a.answerId,
                        answerText = a.answerText,
                        matchingTag = a.matchingTag.name
                    )
                }
            )
        }
    }

    // 추천 알고리즘
    @Transactional(readOnly = true)
    fun getRecommendations(
        request: WizardRecommendRequest,
        sort: String = "distance"
    ): List<PlaceDtoResponse> {

        // A. 사용자가 선택한 답변을 통해 '태그' 목록 추출
        val selectedAnswers = answerRepository.findAllById(request.selectedAnswerIds)
        val selectedTags = selectedAnswers.map { it.matchingTag }.toSet()

        // B. 전체 장소 가져오기
        val allPlaces = placeRepository.findAll()

        // C. [순서 변경] 거리 계산을 먼저 수행 (필터링에 거리가 필요하므로)
        val placesWithDistance = allPlaces.map { place ->
            val dist = if (request.userLatitude != null && request.userLongitude != null) {
                DistanceCalculator.calculate(
                    request.userLatitude, request.userLongitude,
                    place.latitude ?: 0.0, place.longitude ?: 0.0
                )
            } else {
                Double.MAX_VALUE // 위치 정보가 없으면 거리를 무한대로 설정 (거리 필터 시 탈락됨)
            }
            Pair(place, dist)
        }

        // D. 태그와 장소 특성 매칭 (거리 정보 포함하여 필터링)
        val filteredPlaces = placesWithDistance.filter { (place, dist) ->
            isPlaceMatchedWithTags(place, selectedTags, dist)
        }

        // E. 정렬 (거리순, 평점순, 인기순)
        val sortedList = when (sort) {
            "rating" -> filteredPlaces.sortedByDescending { it.first.avgRating } // 평점 높은순
            "popular" -> filteredPlaces.sortedByDescending { it.first.reviewCount } // 리뷰 많은순
            else -> filteredPlaces.sortedBy { it.second } // 거리 가까운순 (기본)
        }

        // F. 상위 3개 변환 후 반환
        if (sortedList.isEmpty()) {
            return emptyList()
        }

        return sortedList.take(3).map { (place, _) ->
            PlaceDtoResponse.from(place)
        }
    }

    /**
     * 🧩 핵심 로직: WizardTag(사용자 답변)가 이 Place에 적합한지 검사
     * (거리 정보도 함께 받아서 판단)
     */
    private fun isPlaceMatchedWithTags(place: Place, tags: Set<WizardTag>, distance: Double): Boolean {

        // 1. 견종 크기 필터 (필수 조건)
        if (tags.contains(WizardTag.SMALL) && !place.allowedSizes.contains(DogSize.SMALL)) return false
        if (tags.contains(WizardTag.MEDIUM) && !place.allowedSizes.contains(DogSize.MEDIUM)) return false
        if (tags.contains(WizardTag.LARGE) && !place.allowedSizes.contains(DogSize.LARGE)) return false

        // 2. [추가됨] 이동 거리 필터
        // - DIST_NEAR: 5km 이내 (5000m)
        if (tags.contains(WizardTag.DIST_NEAR) && distance > 5000) return false
        // - DIST_MID: 20km 이내 (20000m)
        if (tags.contains(WizardTag.DIST_MID) && distance > 20000) return false
        // - DIST_FAR: 20km 이상 (20000m)
        if (tags.contains(WizardTag.DIST_FAR) && distance < 20000) return false

        // 3. 활동량(에너지) 매칭
        if (tags.contains(WizardTag.ENERGY_HIGH)) {
            val isHighEnergyPlace = place.category == PlaceCategory.PLAYGROUND ||
                    place.category == PlaceCategory.SWIMMING ||
                    place.locationType == LocationType.OUTDOOR
            if (!isHighEnergyPlace) return false
        }
        if (tags.contains(WizardTag.ENERGY_LOW)) {
            val isLowEnergyPlace = place.category == PlaceCategory.CAFE ||
                    place.locationType == LocationType.INDOOR
            if (!isLowEnergyPlace) return false
        }

        // 4. 장소 유형 매칭
        if (tags.contains(WizardTag.TYPE_NATURE) && place.locationType == LocationType.INDOOR) return false

        // 5. 프라이빗 선호
        if (tags.contains(WizardTag.TYPE_PRIVATE)) {
            if (place.category != PlaceCategory.ACCOMMODATION && !place.isOffLeash) return false
        }

        return true
    }
}