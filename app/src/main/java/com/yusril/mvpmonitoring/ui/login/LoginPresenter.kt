package com.yusril.mvpmonitoring.ui.login

import com.yusril.mvpmonitoring.core.domain.repository.MainRepository
import com.yusril.mvpmonitoring.core.vo.Resource
import com.yusril.mvpmonitoring.core.vo.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val repository: MainRepository
) : LoginContract.Presenter {

    private lateinit var view: LoginContract.View
    private val _loginState = MutableStateFlow<Resource<String>>(Resource.empty())
    private val loginState : StateFlow<Resource<String>> = _loginState

    var token: String = ""

    fun setView(view: LoginContract.View) {
        this.view = view
    }

    fun init() {
        CoroutineScope(Dispatchers.IO).launch {
            loginState.collect {
                CoroutineScope(Dispatchers.Main).launch {
                    when(it.status) {
                        Status.LOADING -> view.showProgress(true)
                        Status.SUCCESS -> {
                            view.showProgress(false)
                            view.toMain(token)
                        }
                        Status.EMPTY -> view.showProgress(false)
                        Status.ERROR -> {
                            view.showProgress(false)
                            view.showErrorSnackBar(it.message.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onGetToken() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getToken().collect {
                token = it
            }
        }
    }

    override suspend fun onLogin(nidn: String, password: String) {
        onGetToken()
        _loginState.value = Resource.loading()
        val job = CoroutineScope(Dispatchers.IO).launch {
            repository.login(nidn, password)
        }
        job.join()
        _loginState.value = repository.getProfile(token).value
    }
}