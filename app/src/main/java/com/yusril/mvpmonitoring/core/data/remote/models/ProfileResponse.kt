package com.yusril.mvpmonitoring.core.data.remote.models

data class ProfileResponse(
	val user: User
)

data class Dosen(
	val nip: String
)

data class User(
	val dosen: Dosen,
	val name: String,
	val id: Int
)

