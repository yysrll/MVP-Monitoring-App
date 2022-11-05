package com.yusril.mvpmonitoring.ui.splashscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yusril.mvpmonitoring.databinding.ActivitySplashScreenBinding
import com.yusril.mvpmonitoring.ui.login.LoginActivity
import com.yusril.mvpmonitoring.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity(), SplashScreenContract.View {

    @Inject lateinit var presenter: SplashScreenPresenter
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val startTime = System.currentTimeMillis()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        presenter.setView(this)
        presenter.onCheckLogin()

        Log.d("SplashScreen Time: ", "execution time ${System.currentTimeMillis() - startTime} ms")
    }

    override fun toLogin() {
        LoginActivity.start(this@SplashScreenActivity)
        finish()
    }

    override fun toMain(token: String) {
        MainActivity.start(this@SplashScreenActivity, token)
        finish()
    }
}