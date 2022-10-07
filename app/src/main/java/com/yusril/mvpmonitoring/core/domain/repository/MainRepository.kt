package com.yusril.mvpmonitoring.core.domain.repository

import com.yusril.mvpmonitoring.core.domain.model.*
import com.yusril.mvpmonitoring.core.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MainRepository {
    suspend fun getListStudent(token: String, nidn: String): StateFlow<Resource<List<Student>>>
    suspend fun getStudyResult(token: String, nim: String, semester_code: String?): StateFlow<Resource<List<StudyResult>>>
    suspend fun getSemester(token: String): StateFlow<Resource<List<Semester>>>

    suspend fun login(nidn: String, password: String): StateFlow<Resource<String>>
    suspend fun getProfile(token: String): StateFlow<Resource<String>>

    fun getToken(): Flow<String>
    fun getCurrentLecturer(): Flow<Lecturer>
    suspend fun deleteLecturer()
}