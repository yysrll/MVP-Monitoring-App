package com.yusril.mvpmonitoring.utils

import com.yusril.mvpmonitoring.core.data.remote.models.*
import com.yusril.mvpmonitoring.core.domain.model.Semester
import com.yusril.mvpmonitoring.core.domain.model.Student
import com.yusril.mvpmonitoring.core.domain.model.StudyResult

object DataMapper {

    fun mapStudentResponseToStudent(input: List<StudentResponse>) : List<Student> {
        val listStudent = ArrayList<Student>()
        input.map {
            val student = Student(
                id = it.id_mahasiswa,
                nim = it.nim,
                gpa = it.ipk,
                sks = it.jumlah_sks
            )
            listStudent.add(student)
        }
        return listStudent
    }

    fun mapStudyResultResponseToStudyResult(input: List<StudyResultResponse>) : List<StudyResult> {
        val listStudyResult = ArrayList<StudyResult>()
        input.map {
            val studyResult = StudyResult(
                name = it.nama_mata_kuliah,
                score_letter = it.nilai_huruf,
                score_number = it.nilai_angka,
                sks = it.sks,
                score_total = it.total_nilai
            )
            listStudyResult.add(studyResult)
        }
        return listStudyResult
    }

    fun mapListSemesterResponseToListSemester(list: List<SemesterResponse>) : List<Semester> {
        val listSemester = ArrayList<Semester>()
        list.map {
            val semester = Semester(
                jenis = it.jenis,
                kode = it.kode,
                tahun = it.tahun,
                tahun_ajaran = it.tahun_ajaran,
            )
            listSemester.add(semester)
        }
        return listSemester
    }
}