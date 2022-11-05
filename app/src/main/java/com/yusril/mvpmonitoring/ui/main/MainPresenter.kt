package com.yusril.mvpmonitoring.ui.main

import android.util.Log
import com.yusril.mvpmonitoring.core.domain.repository.MainRepository
import com.yusril.mvpmonitoring.core.vo.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val repository: MainRepository
) : MainContract.Presenter {

    private lateinit var view: MainContract.View

    fun setView(view: MainContract.View) {
        this.view = view
    }

    override fun onGetLecturer(token: String) {
        view.showProgress(true)
        CoroutineScope(Dispatchers.IO).launch {
            val lecturer = repository.getCurrentLecturer()
            lecturer.collect {
                withContext(Dispatchers.Main) {
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
    }

    override fun onGetStudent(token: String, nidn: String) {
        view.showProgress(true)
        CoroutineScope(Dispatchers.IO).launch {
            val resource = repository.getListStudent(token, nidn)
            withContext(Dispatchers.Main) {
                when (resource.status) {
                    Status.LOADING -> {}
                    Status.SUCCESS -> {
                        view.showProgress(false)
                        resource.data?.let {
                            view.setListStudent(it)
                        }
                    }
                    Status.EMPTY -> {
                        view.showProgress(false)
                        view.showEmptyView(true)
                    }
                    Status.ERROR -> {
                        view.showProgress(false)
                        view.showStudentErrorAlert()
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