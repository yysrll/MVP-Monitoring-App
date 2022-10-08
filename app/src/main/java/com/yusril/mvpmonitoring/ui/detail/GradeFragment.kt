package com.yusril.mvpmonitoring.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.yusril.mvpmonitoring.R
import com.yusril.mvpmonitoring.core.domain.model.Semester
import com.yusril.mvpmonitoring.core.domain.model.Student
import com.yusril.mvpmonitoring.core.domain.model.StudyResult
import com.yusril.mvpmonitoring.core.presentation.SubjectAdapter
import com.yusril.mvpmonitoring.databinding.FragmentGradeBinding
import com.yusril.mvpmonitoring.ui.detail.DetailActivity.Companion.EXTRA_STUDENT
import com.yusril.mvpmonitoring.ui.detail.DetailActivity.Companion.EXTRA_TOKEN
import com.yusril.mvpmonitoring.ui.detail.DetailActivity.Companion.TAB_TITLES
import com.yusril.mvpmonitoring.ui.utils.MyAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GradeFragment : Fragment(), DetailContract.View {

    @Inject lateinit var presenter: DetailPresenter
    private lateinit var binding: FragmentGradeBinding
    private lateinit var student: Student
    private lateinit var token: String
    private lateinit var subjectAdapter: SubjectAdapter
    private var index: Int = 0
    private var semesterCode: String? = null
    private var semesterItems: List<Semester> = listOf()

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

        index = requireArguments().getInt(ARG_SECTION_NUMBER, 0)
        student = activity?.intent?.getParcelableExtra(EXTRA_STUDENT)!!
        token = activity?.intent?.getStringExtra(EXTRA_TOKEN).toString()

        presenter.setView(this)
        presenter.init(index)
        presenter.onGetStudyResult(
            token,
            student.nim,
            semesterCode,
            index
        )
        presenter.onGetSemester(token)
    }

    override fun initRecyclerView() {
        subjectAdapter = SubjectAdapter()
        val rv = binding.rvSubjects
        rv.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = subjectAdapter
        }
    }

    override fun showProgress(isLoading: Boolean) {
        binding.rvSubjects.isInvisible = isLoading
        binding.progressBar.isInvisible = !isLoading
    }

    override fun showEmptyView(isEmpty: Boolean) {
        binding.emptyStatus.root.isInvisible = !isEmpty
        binding.rvSubjects.isInvisible = isEmpty
        binding.tvTotalSksValue.text = "0"
        binding.tvTotalSubjectValue.text = "0"
        binding.tvTotalGpaValue.text = "0"
    }

    override fun showErrorAlert() {
        MyAlertDialog().setErrorAlertDialog(
            requireActivity(),
            resources.getString(R.string.error_dialog_title),
            resources.getString(R.string.error_dialog_description),
            resources.getString(R.string.try_again)
        ) {
            presenter.onGetSemester(token)
        }.show()
    }

    override fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun showSemester() {
        binding.menuSemesterLayout.isVisible = index == 1
        if (index == 1) {
            binding.menuSemester.apply {
                setOnItemClickListener { _, _, position, _ ->
                    Log.d(TAG, "onClick position: $position")
                    semesterCode = semesterItems[position].kode
                    presenter.onGetStudyResult(token, student.nim, semesterCode, index)
                }
            }
        }
    }

    override fun setListStudyResult(list: List<StudyResult>) {
        subjectAdapter.addStudySubject(list)
        val sks = list.sumOf { it.sks }
        val scoreTotal = list.sumOf { it.score_total.toDouble() }
        val gpa = (scoreTotal / sks)
            .toBigDecimal()
            .setScale(2, java.math.RoundingMode.HALF_UP)
        val subject = list.size
        binding.tvTotalSksValue.text = sks.toString()
        binding.tvTotalSubjectValue.text = subject.toString()
        binding.tvTotalGpaValue.text = gpa.toString()
    }

    override fun setListSemester(semesters: List<Semester>) {
        semesterItems = semesters
        val semesterString = semesters.map {
            "Semester " + it.jenis + " " + it.tahun_ajaran
        }
        Log.d("$TAG ${TAB_TITLES[0]}", "Semester: $semesterString")
        val myAdapter = ArrayAdapter(
            requireContext(),
            R.layout.semester_list_item,
            semesterString
        )

        semesterCode = semesters[0].kode
        Log.d(TAG, "Semester code: $semesterCode")

        binding.menuSemester.apply {
            setAdapter(myAdapter)
            setText(semesterString[0], false)
            setOnItemClickListener { _, _, position, _ ->
                Log.d(TAG, "onClick position: $position")
                semesterCode = semesterItems[position].kode
                presenter.onGetStudyResult(token, student.nim, semesterCode, index)
            }
        }
        presenter.onGetStudyResult(token, student.nim, semesterCode, index)
    }

    companion object {
        val TAG: String = GradeFragment::class.java.simpleName
        const val ARG_SECTION_NUMBER = "section_number"
    }
}