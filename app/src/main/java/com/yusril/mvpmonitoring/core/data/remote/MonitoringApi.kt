package com.yusril.mvpmonitoring.core.data.remote

import com.yusril.mvpmonitoring.core.data.remote.models.*
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface MonitoringApi {

    @GET("service/list-mahasiswa-bimbingan")
    suspend fun getStudent(
        @Header("Authorization") token: String,
        @Query("nidn") nidn: String
    ): Response<ListStudentResponse>

    @GET("service/hasil-studi-mahasiswa")
    suspend fun getStudyResult(
        @Header("Authorization") token: String,
        @Query("nim") nim: String,
        @Query("kode_semester") semester_code: String? = null
    ): Response<ListStudyResultResponse>

    @GET("service/semester")
    suspend fun getSemester(
        @Header("Authorization") token: String
    ): Response<ListSemesterResponse>

    @FormUrlEncoded
    @POST("service/login")
    suspend fun login(
        @Field("nidn") nidn: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("service/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ) : Response<ProfileResponse>
}