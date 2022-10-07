package com.yusril.mvpmonitoring.core.data.remote.models

data class StudyResultResponse(
    val nama_mata_kuliah: String,
    val nilai_angka: String,
    val nilai_huruf: String,
    val sks: Int,
    val total_nilai: String
)