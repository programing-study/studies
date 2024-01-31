package com.study.web

import com.study.entity.Study
import com.study.log
import com.study.repository.StudyRepository
import kotlinx.coroutines.Dispatchers
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StudyController(
    private val studyRepository: StudyRepository
) {
    @GetMapping("/study")
    suspend fun getStudy(): List<Study> {
        log.info { " call " }
        val study = Dispatchers.IO.run {
            studyRepository.findAll()
        }

        return study.toList()
    }
}
