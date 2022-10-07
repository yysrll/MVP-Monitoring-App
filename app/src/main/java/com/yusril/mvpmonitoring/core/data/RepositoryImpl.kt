package com.yusril.mvpmonitoring.core.data

import android.util.Log
import com.yusril.mvpmonitoring.core.data.local.PreferenceDataSource
import com.yusril.mvpmonitoring.core.data.remote.MonitoringApi
import com.yusril.mvpmonitoring.core.domain.model.*
import com.yusril.mvpmonitoring.core.domain.repository.MainRepository
import com.yusril.mvpmonitoring.core.vo.Resource
import com.yusril.mvpmonitoring.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: MonitoringApi,
    private val local: PreferenceDataSource
) : MainRepository {

    override suspend fun getListStudent(
        token: String,
        nidn: String
    ): StateFlow<Resource<List<Student>>> {
        val result = MutableStateFlow<Resource<List<Student>>>(Resource.loading())
        Log.d("Repository", "getListStudent $token")
        try {
            val response = api.getStudent(token, nidn)
            val responseBody = response.body()
            if (response.isSuccessful) {
                if (responseBody?.mahasiswas!!.isNotEmpty()) {
                    val listStudent = DataMapper.mapStudentResponseToStudent(responseBody.mahasiswas)
                    result.value = Resource.success(listStudent)
                    Log.d("Repository", "getListStudent success")
                } else {
                    result.value = Resource.empty()
                    Log.d("Repository", "getListStudent empty")
                }
            } else {
                result.value = Resource.error(response.message())
            }
        } catch (e: Exception) {
            Log.d("Repository", "getListStudent error")
            result.value = Resource.error(e.message ?: "Something went wrong")
        }
        return result
    }

    override suspend fun getStudyResult(
        token: String,
        nim: String,
        semester_code: String?
    ): StateFlow<Resource<List<StudyResult>>> {
        val result = MutableStateFlow<Resource<List<StudyResult>>>(Resource.empty())
        try {
            val response = api.getStudyResult(token, nim, semester_code)
            val responseBody = response.body()
            if (response.isSuccessful) {
                if (responseBody?.kartu_hasil_studi!!.isNotEmpty()) {
                    val listStudyResult = DataMapper.mapStudyResultResponseToStudyResult(responseBody.kartu_hasil_studi)
                    result.value = Resource.success(listStudyResult)
                } else {
                    result.value = Resource.empty()
                }
            } else {
                result.value = Resource.error(response.message())
            }
        } catch (e: Exception) {
            result.value = Resource.error(e.message ?: "Something went wrong")
        }

        return result
    }

    override suspend fun getSemester(token: String): StateFlow<Resource<List<Semester>>> {
        val result = MutableStateFlow<Resource<List<Semester>>>(Resource.empty())
        try {
            val response = api.getSemester(token)
            val responseBody = response.body()
            if (response.isSuccessful) {
                if (responseBody?.semesters!!.isNotEmpty()) {
                    result.value = Resource.success(
                        DataMapper.mapListSemesterResponseToListSemester(responseBody.semesters)
                    )
                } else {
                    result.value = Resource.empty()
                }
            } else {
                result.value = Resource.error(response.message())
            }
        } catch (e: Exception) {
            result.value = Resource.error(e.message ?: "Something went wrong")
        }

        return result
    }

    override suspend fun login(nidn: String, password: String): StateFlow<Resource<String>> {
        val result = MutableStateFlow<Resource<String>>(Resource.empty())
        try {
            val response = api.login(nidn, password)
            val responseBody = response.body()
            if (response.isSuccessful) {
                if (responseBody?.access_token!!.isNotEmpty()) {
                    local.setToken(responseBody.access_token)
                    result.value = Resource.success("Login successfully")
                } else {
                    result.value = Resource.empty()
                }
            } else {
                result.value = Resource.error(response.message())
            }
        } catch (e: Exception) {
            result.value = Resource.error(e.message ?: "Something went wrong")
            println("error login ${e.message}")
        }

        return result
    }

    override suspend fun getProfile(token: String): StateFlow<Resource<String>> {
        val result = MutableStateFlow<Resource<String>>(Resource.loading())
        Log.d("Repository", "getProfile token $token")
        try {
            val response = api.getProfile(token)
            val responseBody = response.body()
            if (response.isSuccessful) {
                val lecturer = Lecturer(
                    responseBody!!.user.name,
                    responseBody.user.dosen.nip
                )
                local.setNewLecturer(lecturer)
                result.value = Resource.success("Get Profile Successfully")
            } else {
                result.value = Resource.error("get profile failed ${response.code()}")
            }
        } catch (e: Exception) {
            result.value = Resource.error(e.message ?: "Something went wrong")
            Log.d("Repository", "getProfile error ${e.message}")
        }

        return result
    }

    override fun getToken(): Flow<String> = local.getToken()
    override fun getCurrentLecturer(): Flow<Lecturer> = local.getCurrentUser()
    override suspend fun deleteLecturer() = local.deleteLecturer()
}