package com.yusril.mvpmonitoring.ui.main

import com.yusril.mvpmonitoring.core.domain.model.Lecturer
import com.yusril.mvpmonitoring.core.domain.model.Student

interface MainContract {
    interface View {
        fun initRecyclerView()
        fun showProgress(isLoading: Boolean)
        fun showEmptyView(isEmpty: Boolean)
        fun showLecturerErrorAlert()
        fun showStudentErrorAlert()
        fun setListStudent(list: List<Student>)
        fun setLecturerProfile(lecturer: Lecturer)
        fun logout()
    }
    interface Presenter {
        fun onGetLecturer(token: String)
        fun onGetStudent(token: String, nidn: String)
        fun onLogout()
    }
}