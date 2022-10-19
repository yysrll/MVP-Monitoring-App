package com.yusril.mvpmonitoring.ui.detail

import com.yusril.mvpmonitoring.core.domain.repository.MainRepository
import com.yusril.mvpmonitoring.core.vo.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DetailPresenter @Inject constructor(
    private val repository: MainRepository
) : DetailContract.Presenter{

    private lateinit var view: DetailContract.View

    fun setView(view: DetailContract.View) {
        this.view = view
    }

    fun init() {
        view.initRecyclerView()
        view.showSemester()
    }

    override fun onGetStudyResult(token: String, nim: String, semester_code: String?, index: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val studyResult = if (index == 1) {
                repository.getStudyResult(token, nim, semester_code)
            } else {
                repository.getStudyResult(token, nim, null)
            }

            withContext(Dispatchers.Main) {
                when (studyResult.status) {
                    Status.LOADING -> {
                        view.showProgress(true)
                        view.showEmptyView(false)
                    }
                    Status.SUCCESS -> {
                        view.showProgress(false)
                        view.showEmptyView(false)
                        studyResult.data?.let { view.setListStudyResult(it) }
                    }
                    Status.EMPTY -> {
                        view.showProgress(false)
                        view.showEmptyView(true)
                    }
                    Status.ERROR -> {
                        view.showProgress(false)
                        view.showEmptyView(false)
                        view.showErrorAlert()
                    }
                }
            }
        }
    }

    override fun onGetSemester(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val semester = repository.getSemester(token)
            withContext(Dispatchers.Main) {
                when (semester.status) {
                    Status.LOADING -> TODO()
                    Status.SUCCESS -> {
                        semester.data?.let {
                            view.setListSemester(it)
                        }
                    }
                    Status.EMPTY -> view.showToast("Data semester tidak ada")
                    Status.ERROR -> view.showErrorAlert()
                }
            }
        }
    }
}