package com.yusril.mvpmonitoring.ui.detail

import com.yusril.mvpmonitoring.core.domain.model.Semester
import com.yusril.mvpmonitoring.core.domain.model.StudyResult

interface DetailContract {
    interface View {
        fun initRecyclerView()
        fun showProgress(isLoading: Boolean)
        fun showEmptyView(isEmpty: Boolean)
        fun showErrorAlert()
        fun showToast(message: String)
        fun showSemester()
        fun setListStudyResult(list: List<StudyResult>)
        fun setListSemester(semesters: List<Semester>)
    }
    interface Presenter {
        fun onGetStudyResult(token: String, nim: String, semester_code: String? = null, index: Int)
        fun onGetSemester(token: String)
    }
}