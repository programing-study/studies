package com.study.repository

import com.study.entity.Study
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class WrapperStudyRepository(
    private val studyRepository: StudyRepository
) {
    suspend fun getStudy(): List<Study> = coroutineScope {
        async {
            studyRepository.findAll()
        }.await().toList()
    }
}
