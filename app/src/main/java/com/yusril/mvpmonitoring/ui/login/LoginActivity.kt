package com.yusril.mvpmonitoring.ui.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import com.google.android.material.snackbar.Snackbar
import com.yusril.mvpmonitoring.R
import com.yusril.mvpmonitoring.databinding.ActivityLoginBinding
import com.yusril.mvpmonitoring.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), LoginContract.View {

    @Inject lateinit var presenter: LoginPresenter
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        presenter.setView(this)
        presenter.init()

        binding.btnLogin.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                presenter.onLogin(
                    binding.etNip.text.toString(),
                    binding.etPassword.text.toString()
                )
            }
        }
    }

    override fun showProgress(isLoading: Boolean) {
        binding.btnLogin.isInvisible = isLoading
        binding.pbLogin.isInvisible = !isLoading
    }

    override fun showErrorSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.danger_200))
            .setTextColor(ContextCompat.getColor(applicationContext, R.color.danger_700))
            .show()
    }

    override fun toMain(token: String) {
        MainActivity.start(this@LoginActivity, token)
        finish()
    }

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, LoginActivity::class.java))
        }
    }
}