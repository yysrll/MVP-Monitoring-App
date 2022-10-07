package com.yusril.mvpmonitoring.core.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yusril.mvpmonitoring.R
import com.yusril.mvpmonitoring.core.domain.model.StudyResult
import com.yusril.mvpmonitoring.databinding.ItemRowSubjectBinding

class SubjectAdapter: RecyclerView.Adapter<SubjectAdapter.RecyclerViewHolder>() {

    private val listSubject = ArrayList<StudyResult>()

    @SuppressLint("NotifyDataSetChanged")
    fun addStudySubject(items: List<StudyResult>) {
        listSubject.clear()
        listSubject.addAll(items)
        this.notifyDataSetChanged()
    }

    class RecyclerViewHolder(private val binding: ItemRowSubjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudyResult) {
            val bgGrade = when (item.score_letter) {
                "A", "A-" -> R.drawable.bg_grade_a
                "B+", "B", "B-" -> R.drawable.bg_grade_b
                "C+", "C" -> R.drawable.bg_grade_c
                "D" -> R.drawable.bg_grade_d
                else -> R.drawable.bg_grade_e
            }
            binding.rvSubjectName.text = item.name
            binding.rvSubjectSks.text = this.itemView.context.getString(R.string.total_sks, item.sks)
            binding.rvSubjectGrade.apply {
                text = item.score_letter
                background = context.getDrawable(bgGrade)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = ItemRowSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bind(listSubject[position])

    override fun getItemCount(): Int = listSubject.size
}