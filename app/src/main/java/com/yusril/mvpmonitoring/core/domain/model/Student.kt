package com.yusril.mvpmonitoring.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(
    var id: Int,
    var nim: String,
    var gpa: String,
    var sks: Int
) : Parcelable
