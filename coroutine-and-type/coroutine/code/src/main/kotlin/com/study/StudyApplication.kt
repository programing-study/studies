package com.study

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

val log = KotlinLogging.logger { }

@SpringBootApplication
class StudyApplication

fun main(args: Array<String>) {
    runApplication<StudyApplication>(*args)
    
    log.info { "Server has been started." }
}
