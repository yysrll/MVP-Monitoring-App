package com.yusril.mvpmonitoring.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yusril.mvpmonitoring.core.domain.model.Semester
import com.yusril.mvpmonitoring.core.domain.model.StudyResult
import com.yusril.mvpmonitoring.core.domain.repository.MainRepository
import com.yusril.mvpmonitoring.core.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _listStudyGrade = MutableStateFlow<Resource<List<StudyResult>>>(Resource.empty())
    val listStudyGrade : StateFlow<Resource<List<StudyResult>>> = _listStudyGrade

    private val _listSemester = MutableStateFlow<Resource<List<Semester>>>(Resource.empty())
    val listSemester : StateFlow<Resource<List<Semester>>> = _listSemester



    fun getListStudyGrade(token: String, nim: String, semester_code: String? = null) {
        _listStudyGrade.value = Resource.loading()
        viewModelScope.launch(Dispatchers.IO) {
            _listStudyGrade.value = repository.getStudyResult(token, nim, semester_code).value
        }
    }

    fun getSemester(token: String) {
        _listSemester.value = Resource.loading()
        viewModelScope.launch(Dispatchers.IO) {
            _listSemester.value = repository.getSemester(token).value
        }
    }
}