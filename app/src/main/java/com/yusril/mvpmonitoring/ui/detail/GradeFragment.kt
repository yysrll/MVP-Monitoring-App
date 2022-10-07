package com.yusril.mvpmonitoring.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yusril.mvpmonitoring.R
import com.yusril.mvpmonitoring.core.domain.model.Semester
import com.yusril.mvpmonitoring.core.domain.model.Student
import com.yusril.mvpmonitoring.core.presentation.SubjectAdapter
import com.yusril.mvpmonitoring.core.vo.Status
import com.yusril.mvpmonitoring.databinding.FragmentGradeBinding
import com.yusril.mvpmonitoring.ui.detail.DetailActivity.Companion.EXTRA_STUDENT
import com.yusril.mvpmonitoring.ui.detail.DetailActivity.Companion.EXTRA_TOKEN
import com.yusril.mvpmonitoring.ui.detail.DetailActivity.Companion.TAB_TITLES
import com.yusril.mvpmonitoring.ui.utils.MyAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GradeFragment : Fragment() {

    private lateinit var errorAlertDialog: AlertDialog
    private lateinit var binding: FragmentGradeBinding
    private lateinit var student: Student
    private lateinit var token: String
    private lateinit var subjectAdapter: SubjectAdapter
    private var semesterCode: String? = null
    private var semesterItems: List<Semester> = listOf()
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGradeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val index = requireArguments().getInt(ARG_SECTION_NUMBER, 0)
        student = activity?.intent?.getParcelableExtra(EXTRA_STUDENT)!!
        token = activity?.intent?.getStringExtra(EXTRA_TOKEN).toString()

        initRecyclerView()
        setErrorAlertDialog(index)
        showSemester(index)

        lifecycleScope.launch {
            viewModel.listStudyGrade.collect { listStudyResult ->
                when (listStudyResult.status) {
                    Status.LOADING -> {
                        showLoading(true)
                        showEmptyView(false)
                        Log.d("$TAG ${TAB_TITLES[index-1]}", "Loading")
                    }
                    Status.SUCCESS -> {
                        showLoading(false)
                        showEmptyView(false)
                        Log.d("$TAG ${TAB_TITLES[index-1]}", "Success: ${listStudyResult.data}")
                        listStudyResult.data?.let { data ->
                            subjectAdapter.addStudySubject(data)
                            val sks = data.sumOf { it.sks }
                            val scoreTotal = data.sumOf { it.score_total.toDouble() }
                            val gpa = (scoreTotal / sks)
                                .toBigDecimal()
                                .setScale(2, java.math.RoundingMode.HALF_UP)
                            val subject = data.size
                            binding.tvTotalSksValue.text = sks.toString()
                            binding.tvTotalSubjectValue.text = subject.toString()
                            binding.tvTotalGpaValue.text = gpa.toString()
                        }
                    }
                    Status.EMPTY -> {
                        showLoading(false)
                        showEmptyView(true)
                        Log.d("$TAG ${TAB_TITLES[index-1]}", "Empty")
                    }
                    Status.ERROR -> {
                        showLoading(false)
                        showEmptyView(false)
                        errorAlertDialog.show()
                        Log.d("$TAG ${TAB_TITLES[index-1]}", "Error: ${listStudyResult.message}")
                    }
                }
            }
        }

        if (index == 1) {
            lifecycleScope.launch {
                viewModel.listSemester.collect { listSemesterResult ->
                    when (listSemesterResult.status) {
                        Status.SUCCESS -> {
                            Log.d(TAG, "Semester success: ${listSemesterResult.data}")
                            listSemesterResult.data?.let { data ->
                                semesterItems = data
                                val semesterString = data.map {
                                    "Semester " + it.jenis + " " + it.tahun_ajaran
                                }
                                Log.d("$TAG ${TAB_TITLES[0]}", "Semester: $semesterString")
                                val myAdapter = ArrayAdapter(
                                    requireContext(),
                                    R.layout.semester_list_item,
                                    semesterString
                                )
                                binding.menuSemester.apply {
                                    setAdapter(myAdapter)
                                    setText(semesterString[0], false)
                                }
                                semesterCode = data[0].kode
                                Log.d(TAG, "Semester code: $semesterCode")
                                getListStudyGrade(index, student, semesterCode)
                            }
                        }
                        Status.EMPTY -> {
                            Log.d(TAG, "Semester Empty")
                        }
                        else -> {
                            Log.d(TAG, "Semester error: ${listSemesterResult.message}")
                            if (listSemesterResult.status == Status.ERROR) errorAlertDialog.show()
                        }
                    }
                }
            }
        }
    }

    private fun showSemester(index: Int) {
        binding.menuSemesterLayout.isVisible = index != 2
        if (index == 1) {
            setSemester(index)
        }
        getListStudyGrade(index, student, semesterCode)
    }

    private fun setSemester(index: Int) {
        viewModel.getSemester(token)

        binding.menuSemester.apply {
            setOnItemClickListener { _, _, position, _ ->
                Log.d(TAG, "onClick position: $position")
                semesterCode = semesterItems[position].kode
                getListStudyGrade(index, student, semesterCode)
            }
        }
    }

    private fun showEmptyView(isEmpty: Boolean) {
        binding.emptyStatus.root.isInvisible = !isEmpty
        binding.rvSubjects.isInvisible = isEmpty
        binding.tvTotalSksValue.text = "0"
        binding.tvTotalSubjectValue.text = "0"
        binding.tvTotalGpaValue.text = "0"
    }

    private fun setErrorAlertDialog(index: Int) {
        errorAlertDialog = MyAlertDialog().setErrorAlertDialog(
            requireActivity(),
            resources.getString(R.string.error_dialog_title),
            resources.getString(R.string.error_dialog_description),
            resources.getString(R.string.try_again)
        ) {
            setSemester(index)
        }
    }

    private fun initRecyclerView() {
        subjectAdapter = SubjectAdapter()
        val rv = binding.rvSubjects
        rv.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = subjectAdapter
        }
    }

    private fun getListStudyGrade(index: Int, student: Student, code: String?) {
        when(index) {
            1 -> viewModel.getListStudyGrade(token, student.nim, code)
            2 -> viewModel.getListStudyGrade(token, student.nim)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.rvSubjects.isInvisible = isLoading
        binding.progressBar.isInvisible = !isLoading
    }

    companion object {
        val TAG: String = GradeFragment::class.java.simpleName
        const val ARG_SECTION_NUMBER = "section_number"
    }
}