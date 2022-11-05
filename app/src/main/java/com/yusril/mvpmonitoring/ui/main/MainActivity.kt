package com.yusril.mvpmonitoring.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.yusril.mvpmonitoring.R
import com.yusril.mvpmonitoring.core.domain.model.Lecturer
import com.yusril.mvpmonitoring.core.domain.model.Student
import com.yusril.mvpmonitoring.core.presentation.StudentAdapter
import com.yusril.mvpmonitoring.databinding.ActivityMainBinding
import com.yusril.mvpmonitoring.databinding.StudentListBinding
import com.yusril.mvpmonitoring.ui.detail.DetailActivity
import com.yusril.mvpmonitoring.ui.login.LoginActivity
import com.yusril.mvpmonitoring.ui.utils.MyAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainContract.View {

    @Inject lateinit var presenter: MainPresenter
    private lateinit var binding: ActivityMainBinding
    private lateinit var studentListBinding: StudentListBinding
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var lecturer: Lecturer
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startTime = System.currentTimeMillis()
        binding = ActivityMainBinding.inflate(layoutInflater)
        studentListBinding = StudentListBinding.bind(binding.root)
        setContentView(binding.root)

        presenter.setView(this)

        supportActionBar?.apply {
            title = ""
            elevation = 0f
            setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.primary_500
                    )
                )
            )
        }

        token = intent.getStringExtra(TOKEN_EXTRA).toString()
        presenter.onGetLecturer(token)

        initRecyclerView()

        Log.d("MainActivity Time: ", "execution time ${System.currentTimeMillis() - startTime} ms")
    }

    override fun initRecyclerView() {
        studentAdapter = StudentAdapter()
        val rv = studentListBinding.rvStudents
        rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = studentAdapter
            setHasFixedSize(true)
        }
        studentAdapter.setOnItemClickCallback(object : StudentAdapter.OnItemClickCallback {
            override fun onItemClicked(student: Student) {
                DetailActivity.start(this@MainActivity, student, token)
            }
        })
    }

    override fun showProgress(isLoading: Boolean) {
        studentListBinding.rvStudents.isInvisible = isLoading
        studentListBinding.progressBar.isInvisible = !isLoading
    }

    override fun showEmptyView(isEmpty: Boolean) {
        studentListBinding.emptyStatus.root.isInvisible = !isEmpty
    }

    override fun showLecturerErrorAlert() {
        MyAlertDialog().setErrorAlertDialog(
            this@MainActivity,
            resources.getString(R.string.error_dialog_title),
            resources.getString(R.string.error_dialog_get_lecturer),
            resources.getString(R.string.login_again),
        ) {
            presenter.onLogout()
        }.show()
    }

    override fun showStudentErrorAlert() {
        MyAlertDialog().setErrorAlertDialog(
            this,
            resources.getString(R.string.error_dialog_title),
            resources.getString(R.string.error_dialog_description),
            resources.getString(R.string.try_again)
        ) {
            presenter.onGetStudent(token, lecturer.nidn)
        }.show()
    }

    override fun setListStudent(list: List<Student>) {
        studentAdapter.addStudents(list)
    }

    override fun setLecturerProfile(lecturer: Lecturer) {
        this.lecturer = lecturer
        binding.tvLecturerName.text = lecturer.name
    }

    override fun logout() {
        LoginActivity.start(this)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                MyAlertDialog().setActionAlertDialog(
                    this,
                    resources.getString(R.string.logout_confirm),
                    resources.getString(R.string.logout),
                ) {
                    presenter.onLogout()
                }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    companion object {
        private const val TOKEN_EXTRA = "TOKEN"
        fun start(activity: Activity, token: String) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra(TOKEN_EXTRA, token)
            activity.startActivity(intent)
        }
    }
}