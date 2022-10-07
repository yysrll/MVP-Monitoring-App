package com.yusril.mvpmonitoring.ui.detail

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.yusril.mvpmonitoring.core.domain.model.Student
import com.yusril.mvpmonitoring.core.presentation.SectionsPagerAdapter
import com.yusril.mvpmonitoring.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var student: Student
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarTop)
        supportActionBar?.apply {
            elevation = 0f
            title = null
            setDisplayHomeAsUpEnabled(true)
        }

        student = intent.getParcelableExtra(EXTRA_STUDENT)!!
        token = intent.getStringExtra(EXTRA_TOKEN).toString()

        binding.detailStudentNim.text = student.nim
        binding.detailStudentGpa.text = "IPK ${student.gpa}"

        setupPagerAdapter()
    }

    private fun setupPagerAdapter() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.detailViewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.detailTabs, binding.detailViewPager) { tab, position ->
            tab.text = TAB_TITLES[position]
        }.attach()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        val TAB_TITLES = arrayOf(
            "KHS",
            "TRANSKRIP"
        )
        const val EXTRA_STUDENT = "extra_student"
        const val EXTRA_TOKEN = "extra_token"
        fun start(activity: Activity, student: Student, token: String) {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(EXTRA_STUDENT, student)
            intent.putExtra(EXTRA_TOKEN, token)
            activity.startActivity(intent)
        }
    }
}