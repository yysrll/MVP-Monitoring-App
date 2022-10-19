package com.yusril.mvpmonitoring.core.domain.repository

import com.yusril.mvpmonitoring.core.domain.model.Lecturer
import com.yusril.mvpmonitoring.core.domain.model.Semester
import com.yusril.mvpmonitoring.core.domain.model.Student
import com.yusril.mvpmonitoring.core.domain.model.StudyResult
import com.yusril.mvpmonitoring.core.vo.Resource
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun getListStudent(token: String, nidn: String): Resource<List<Student>>
    suspend fun getStudyResult(token: String, nim: String, semester_code: String?): Resource<List<StudyResult>>
    suspend fun getSemester(token: String): Resource<List<Semester>>

    suspend fun login(nidn: String, password: String): Resource<String>
    suspend fun getProfile(token: String): Resource<String>

    fun getToken(): Flow<String>
    fun getCurrentLecturer(): Flow<Lecturer>
    suspend fun deleteLecturer()
}