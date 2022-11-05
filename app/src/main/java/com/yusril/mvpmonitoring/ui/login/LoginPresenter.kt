package com.yusril.mvpmonitoring.ui.login

import com.yusril.mvpmonitoring.core.domain.repository.MainRepository
import com.yusril.mvpmonitoring.core.vo.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val repository: MainRepository
) : LoginContract.Presenter {

    private lateinit var view: LoginContract.View
    private lateinit var token: String

    fun setView(view: LoginContract.View) {
        this.view = view
    }

    override fun onGetToken() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getToken().collect {
                token = it
            }
        }
    }

    override suspend fun onLogin(nidn: String, password: String) {
        withContext(Dispatchers.Main) {
            view.showProgress(true)
        }
        onGetToken()
        val job = CoroutineScope(Dispatchers.IO).launch {
            repository.login(nidn, password)
        }
        job.join()
        repository.getProfile(token).also {
            withContext(Dispatchers.Main) {
                when (it.status) {
                    Status.SUCCESS -> {
                        view.toMain(token)
                    }
                    Status.EMPTY -> view.showErrorSnackBar("token is Empty, try again!")
                    else -> it.message?.let { view.showErrorSnackBar(it) }
                }
                view.showProgress(false)
            }
        }
    }
}