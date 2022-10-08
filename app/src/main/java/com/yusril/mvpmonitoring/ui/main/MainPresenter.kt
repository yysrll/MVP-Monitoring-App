package com.yusril.mvpmonitoring.ui.main

import com.yusril.mvpmonitoring.core.domain.model.Student
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

class MainPresenter @Inject constructor(
    private val repository: MainRepository
) : MainContract.Presenter {

    private lateinit var view: MainContract.View

    private val _students = MutableStateFlow<Resource<List<Student>>>(Resource.loading())
    val students: StateFlow<Resource<List<Student>>> = _students

    fun setView(view: MainContract.View) {
        this.view = view
    }

    override fun onGetLecturer(token: String) {
        view.showProgress(true)
        CoroutineScope(Dispatchers.IO).launch {
            repository.getCurrentLecturer().collect {
                if (it.nidn == "") {
                    withContext(Dispatchers.Main) {
                        view.showLecturerErrorAlert()
                    }
                } else {
                    view.setLecturerProfile(it)
                    onGetStudent(token, it.nidn)
                }
            }
        }
    }

    override fun onGetStudent(token: String, nidn: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _students.value = repository.getListStudent(token, nidn).value
            students.collect {
                withContext(Dispatchers.Main) {
                    when (it.status) {
                        Status.LOADING -> view.showProgress(true)
                        Status.SUCCESS -> {
                            view.showProgress(false)
                            it.data?.let { list -> view.setListStudent(list) }
                        }
                        Status.EMPTY -> view.showEmptyView(true)
                        Status.ERROR -> {
                            view.showProgress(false)
                            view.showStudentErrorAlert()
                        }
                    }
                }
            }
        }
    }

    override fun onLogout() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                view.logout()
            }
            repository.deleteLecturer()
        }
    }
}