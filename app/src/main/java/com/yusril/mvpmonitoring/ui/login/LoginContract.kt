package com.yusril.mvpmonitoring.ui.login

interface LoginContract {
    interface View {
        fun showProgress(isLoading: Boolean)
        fun showErrorSnackBar(message: String)
        fun toMain(token: String)
    }
    interface Presenter {
        fun onGetToken()
        suspend fun onLogin(nidn: String, password: String)
    }
}