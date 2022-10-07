package com.yusril.mvpmonitoring.core.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yusril.mvpmonitoring.ui.detail.GradeFragment
import com.yusril.mvpmonitoring.ui.detail.GradeFragment.Companion.ARG_SECTION_NUMBER

class SectionsPagerAdapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = GradeFragment()
        fragment.arguments = Bundle().apply {
            putInt(ARG_SECTION_NUMBER, position + 1)
        }
        return fragment
    }

}