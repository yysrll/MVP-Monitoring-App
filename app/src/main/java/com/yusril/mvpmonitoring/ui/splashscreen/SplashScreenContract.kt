package com.yusril.mvpmonitoring.ui.splashscreen

interface SplashScreenContract {
    interface View {
        fun toLogin()
        fun toMain(token: String)
    }
    interface Presenter {
        fun onCheckLogin()
    }
}