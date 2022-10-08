package com.yusril.mvpmonitoring.ui.detail

import android.util.Log
import com.yusril.mvpmonitoring.core.domain.model.Semester
import com.yusril.mvpmonitoring.core.domain.model.StudyResult
import com.yusril.mvpmonitoring.core.domain.repository.MainRepository
import com.yusril.mvpmonitoring.core.vo.Resource
import com.yusril.mvpmonitoring.core.vo.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DetailPresenter @Inject constructor(
    private val repository: MainRepository
) : DetailContract.Presenter{

    private lateinit var view: DetailContract.View

    private val _listStudyGrade = MutableStateFlow<Resource<List<StudyResult>>>(Resource.loading())
    private val listStudyGrade : StateFlow<Resource<List<StudyResult>>> = _listStudyGrade

    private val _listSemester = MutableStateFlow<Resource<List<Semester>>>(Resource.loading())
    private val listSemester : StateFlow<Resource<List<Semester>>> = _listSemester

    fun setView(view: DetailContract.View) {
        this.view = view
    }

    fun init(index: Int) {
        view.initRecyclerView()
        view.showSemester()
        if (index == 1) {
            CoroutineScope(Dispatchers.IO).launch {
                listSemester.collect {
                    withContext(Dispatchers.Main) {
                        when (it.status) {
                            Status.LOADING -> {}
                            Status.SUCCESS -> {
                                it.data?.let { semesters -> view.setListSemester(semesters) }
                            }
                            Status.EMPTY -> view.showToast("Data semester tidak ada")
                            else -> view.showErrorAlert()
                        }
                    }
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            listStudyGrade.collect {
                withContext(Dispatchers.Main) {
                    when (it.status) {
                        Status.LOADING -> {
                            view.showProgress(true)
                            view.showEmptyView(false)
                        }
                        Status.SUCCESS -> {
                            view.showProgress(false)
                            view.showEmptyView(false)
                            Log.d("DetailPresenter", it.data.toString())
                            it.data?.let { data -> view.setListStudyResult(data) }
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

    }
    override fun onGetStudyResult(token: String, nim: String, semester_code: String?, index: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _listStudyGrade.value = if (index == 1) {
                repository.getStudyResult(token, nim, semester_code).value
            } else {
                repository.getStudyResult(token, nim, null).value
            }
        }
    }

    override fun onGetSemester(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _listSemester.value = repository.getSemester(token).value
        }
    }
}