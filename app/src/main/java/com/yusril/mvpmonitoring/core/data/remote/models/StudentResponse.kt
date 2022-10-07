package com.yusril.mvpmonitoring.core.data.remote.models

data class StudentResponse(
    val id_mahasiswa: Int,
    val ipk: String,
    val jumlah_sks: Int,
    val kode_dikti_prodi: String,
    val nama_resmi_prodi: String,
    val nim: String,
)