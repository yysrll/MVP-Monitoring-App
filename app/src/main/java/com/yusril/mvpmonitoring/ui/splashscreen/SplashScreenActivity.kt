package com.yusril.mvpmonitoring.ui.splashscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.yusril.mvpmonitoring.databinding.ActivitySplashScreenBinding
import com.yusril.mvpmonitoring.ui.login.LoginActivity
import com.yusril.mvpmonitoring.ui.main.MainActivity
import com.yusril.mvpmonitoring.utils.Constant.SPLASH_SCREEN_DURATION_IN_MILLIS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val viewModel: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                viewModel.token().collect { token ->
                    if (token == "") {
                        LoginActivity.start(this@SplashScreenActivity)
                    } else {
                        MainActivity.start(this@SplashScreenActivity, token)
                    }
                    finish()
                }
            }
        }, SPLASH_SCREEN_DURATION_IN_MILLIS)
    }
}