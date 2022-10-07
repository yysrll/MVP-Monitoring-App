package com.yusril.mvpmonitoring.core.data.remote.models

data class ListStudentResponse(
    val mahasiswas: List<StudentResponse>,
    val message: String
)