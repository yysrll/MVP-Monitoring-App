package com.yusril.mvpmonitoring.ui.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.yusril.mvpmonitoring.R
import com.yusril.mvpmonitoring.core.vo.Status
import com.yusril.mvpmonitoring.databinding.ActivityLoginBinding
import com.yusril.mvpmonitoring.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.etNip.text.toString(),
                binding.etPassword.text.toString()
            )
        }

        lifecycleScope.launch {
            viewModel.loginState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        Log.d(TAG, "Loading")
                        isLoading(true)
                    }
                    Status.SUCCESS -> {
                        MainActivity.start(this@LoginActivity, viewModel.token)
                        finish()
                        isLoading(false)
                    }
                    Status.EMPTY -> {
                        Log.d(TAG, "Empty")
                        isLoading(false)
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "Error: ${it.message}")
                        isLoading(false)
                        Snackbar.make(binding.root, it.message ?: "", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.danger_200))
                            .setTextColor(ContextCompat.getColor(applicationContext, R.color.danger_700))
                            .show()
                    }
                }
            }
        }
    }

    private fun isLoading(state: Boolean) {
        binding.btnLogin.isInvisible = state
        binding.pbLogin.isInvisible = !state
    }

    companion object {
        val TAG: String = LoginActivity::class.java.simpleName
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, LoginActivity::class.java))
        }
    }
}