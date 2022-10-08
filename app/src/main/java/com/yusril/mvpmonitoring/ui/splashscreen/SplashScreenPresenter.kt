package com.yusril.mvpmonitoring.ui.splashscreen

import android.os.Handler
import android.os.Looper
import com.yusril.mvpmonitoring.core.domain.repository.MainRepository
import com.yusril.mvpmonitoring.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SplashScreenPresenter @Inject constructor(
    private val repository: MainRepository
) : SplashScreenContract.Presenter {

    private lateinit var view: SplashScreenContract.View

    fun setView(view: SplashScreenContract.View) {
        this.view = view
    }

    override fun onCheckLogin() {
        Handler(Looper.getMainLooper()).postDelayed({
            CoroutineScope(Dispatchers.IO).launch {
                repository.getToken().collect { token ->
                    withContext(Dispatchers.Main) {
                        if (token == "") {
                            view.toLogin()
                        } else {
                            view.toMain(token)
                        }
                    }
                }
            }
        }, Constant.SPLASH_SCREEN_DURATION_IN_MILLIS)
    }
}