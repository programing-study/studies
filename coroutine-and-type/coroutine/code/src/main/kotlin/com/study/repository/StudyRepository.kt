package com.study.repository

import com.study.entity.Study
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StudyRepository : CrudRepository<Study, Long>
