package com.study.web

import com.study.entity.Study
import com.study.log
import com.study.repository.WrapperStudyRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StudyWrapController(
    private val studyRepository: WrapperStudyRepository
) {
    @GetMapping("/study2")
    suspend fun getStudy(): List<Study> {
        log.info { " call " }
        return studyRepository.getStudy()
    }
}
