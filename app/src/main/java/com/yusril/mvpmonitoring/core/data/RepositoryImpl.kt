package com.yusril.mvpmonitoring.core.data

import android.util.Log
import com.yusril.mvpmonitoring.core.data.local.PreferenceDataSource
import com.yusril.mvpmonitoring.core.data.remote.MonitoringApi
import com.yusril.mvpmonitoring.core.domain.model.*
import com.yusril.mvpmonitoring.core.domain.repository.MainRepository
import com.yusril.mvpmonitoring.core.vo.Resource
import com.yusril.mvpmonitoring.utils.DataMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: MonitoringApi,
    private val local: PreferenceDataSource
) : MainRepository {

    override suspend fun getListStudent(
        token: String,
        nidn: String
    ): Resource<List<Student>> {
        return try {
            val response = api.getStudent(token, nidn)
            val responseBody = response.body()
            if (response.isSuccessful) {
                if (responseBody?.mahasiswas!!.isNotEmpty()) {
                    val listStudent = DataMapper.mapStudentResponseToStudent(responseBody.mahasiswas)
                    Resource.success(listStudent)
                } else {
                    Resource.empty()
                }
            } else {
                Resource.error(response.message())
            }
        } catch (e: Exception) {
            Resource.error(e.message ?: "Something went wrong")
        }
    }

    override suspend fun getStudyResult(
        token: String,
        nim: String,
        semester_code: String?
    ): Resource<List<StudyResult>> {
        return try {
            val response = api.getStudyResult(token, nim, semester_code)
            val responseBody = response.body()
            if (response.isSuccessful) {
                if (responseBody?.kartu_hasil_studi!!.isNotEmpty()) {
                    val listStudyResult = DataMapper.mapStudyResultResponseToStudyResult(responseBody.kartu_hasil_studi)
                    Resource.success(listStudyResult)
                } else {
                    Resource.empty()
                }
            } else {
                Resource.error(response.message())
            }
        } catch (e: Exception) {
            Resource.error(e.message ?: "Something went wrong")
        }
    }

    override suspend fun getSemester(token: String): Resource<List<Semester>> {
        return try {
            val response = api.getSemester(token)
            val responseBody = response.body()
            if (response.isSuccessful) {
                if (responseBody?.semesters!!.isNotEmpty()) {
                    Resource.success(
                        DataMapper.mapListSemesterResponseToListSemester(responseBody.semesters)
                    )
                } else {
                    Resource.empty()
                }
            } else {
                Resource.error(response.message())
            }
        } catch (e: Exception) {
            Resource.error(e.message ?: "Something went wrong")
        }
    }

    override suspend fun login(nidn: String, password: String): Resource<String> {
        return try {
            val response = api.login(nidn, password)
            val responseBody = response.body()
            if (response.isSuccessful) {
                if (responseBody?.access_token!!.isNotEmpty()) {
                    local.setToken(responseBody.access_token)
                    Log.d("RepositoryImpl", "token api: ${responseBody.access_token}")
                    Resource.success("Login successfully")
                } else {
                    Resource.empty()
                }
            } else {
                Resource.error(response.message())
            }
        } catch (e: Exception) {
            Resource.error(e.message ?: "Something went wrong")
        }
    }

    override suspend fun getProfile(token: String): Resource<String> {
        return try {
            val response = api.getProfile(token)
            val responseBody = response.body()
            if (response.isSuccessful) {
                val lecturer = Lecturer(
                    responseBody!!.user.name,
                    responseBody.user.dosen.nip
                )
                local.setNewLecturer(lecturer)
                Resource.success("Get Profile Successfully")
            } else {
                Resource.error("get profile failed ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.error(e.message ?: "Something went wrong")
        }
    }

    override fun getToken(): Flow<String> = local.getToken()
    override fun getCurrentLecturer(): Flow<Lecturer> = local.getCurrentUser()
    override suspend fun deleteLecturer() = local.deleteLecturer()
}