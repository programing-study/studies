package com.study.web

import com.study.entity.Study
import com.study.repository.StudyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StudyController(
    private val studyRepository: StudyRepository
) {

    @GetMapping("/study")
    suspend fun getStudy(): List<Study> = coroutineScope {
        withContext(Dispatchers.IO.limitedParallelism(4)) {
            val result = async {
                studyRepository.findAll()
            }
            result.await().toList()
        }
    }
}
