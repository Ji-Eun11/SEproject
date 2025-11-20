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
        // (예: [SIZE_LARGE, ENERGY_HIGH, TYPE_NATURE])
        val selectedAnswers = answerRepository.findAllById(request.selectedAnswerIds)
        val selectedTags = selectedAnswers.map { it.matchingTag }.toSet()

        // B. 전체 장소 가져오기
        // (데이터가 수만 건이 넘어가면 QueryDSL로 동적 쿼리를 짜야 하지만, 지금은 findAll 후 필터링이 빠름)
        val allPlaces = placeRepository.findAll()

        // C. 태그와 장소 특성 매칭 (필터링 로직)
        val filteredPlaces = allPlaces.filter { place ->
            isPlaceMatchedWithTags(place, selectedTags)
        }

        // D. 거리 계산 (Place와 Distance를 묶어서 처리)
        // request에 userLatitude, userLongitude가 있다고 가정 (DTO 수정 필요)
        val placesWithDistance = filteredPlaces.map { place ->
            val dist = if (request.userLatitude != null && request.userLongitude != null) {
                DistanceCalculator.calculate(
                    request.userLatitude, request.userLongitude,
                    place.latitude ?: 0.0, place.longitude ?: 0.0
                )
            } else {
                Double.MAX_VALUE // 위치 정보 없으면 거리 무한대 처리
            }
            Pair(place, dist)
        }

        // E. 정렬 (거리순, 평점순, 인기순)
        val sortedList = when (sort) {
            "rating" -> placesWithDistance.sortedByDescending { it.first.avgRating } // 평점 높은순
            "popular" -> placesWithDistance.sortedByDescending { it.first.reviewCount } // 리뷰 많은순
            else -> placesWithDistance.sortedBy { it.second } // 거리 가까운순 (기본)
        }

        // F. 상위 3개 변환 후 반환
        // 결과가 없으면 빈 리스트 반환 (에러 대신 빈 화면 처리가 UX상 나을 수 있음)
        if (sortedList.isEmpty()) {
            return emptyList()
        }

        return sortedList.take(3).map { (place, _) ->
            PlaceDtoResponse.from(place)
        }
    }

    /**
     * 🧩 핵심 로직: WizardTag(사용자 답변)가 이 Place에 적합한지 검사
     */
    private fun isPlaceMatchedWithTags(place: Place, tags: Set<WizardTag>): Boolean {

        // 1. 견종 크기 필터 (필수 조건) (사용자가 '소형견'을 선택했는데, 장소가 '소형견'을 허용 안 하면 탈락)
        if (tags.contains(WizardTag.SMALL) && !place.allowedSizes.contains(DogSize.SMALL)) return false
        if (tags.contains(WizardTag.MEDIUM) && !place.allowedSizes.contains(DogSize.MEDIUM)) return false
        if (tags.contains(WizardTag.LARGE) && !place.allowedSizes.contains(DogSize.LARGE)) return false

        // 활동량(에너지) 매칭
        // 에너지가 넘치는 강아지 -> 운동장, 수영장, 야외 선호
        if (tags.contains(WizardTag.ENERGY_HIGH)) {
            val isHighEnergyPlace = place.category == PlaceCategory.PLAYGROUND ||
                    place.category == PlaceCategory.SWIMMING ||
                    place.locationType == LocationType.OUTDOOR
            if (!isHighEnergyPlace) return false
        }
        // 에너지가 적은 강아지 -> 카페, 실내 선호
        if (tags.contains(WizardTag.ENERGY_LOW)) {
            val isLowEnergyPlace = place.category == PlaceCategory.CAFE ||
                    place.locationType == LocationType.INDOOR
            if (!isLowEnergyPlace) return false
        }

        // 장소 유형 매칭 (자연 선호 -> 야외)
        if (tags.contains(WizardTag.TYPE_NATURE) && place.locationType == LocationType.INDOOR) return false

        // 프라이빗 선호 (숙소나, 오프리쉬가 가능한 곳을 추천)
        if (tags.contains(WizardTag.TYPE_PRIVATE)) {
            if (place.category != PlaceCategory.ACCOMMODATION && !place.isOffLeash) return false
        }

        return true
    }
}