package com.study.service

import com.study.entity.Study
import com.study.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.springframework.stereotype.Service

@Service
class StudyService(
    private val studyRepository: StudyRepository
) {
    fun getStudy(): Flow<Study> = studyRepository.findAll().asFlow()
    /*
    suspend fun getStudy2() {
        Flow<Study> = studyRepository.findAll().asFlow()
        //...
        중단 함수를 이용한 계산한 다음에
        return
    }
     */
}
