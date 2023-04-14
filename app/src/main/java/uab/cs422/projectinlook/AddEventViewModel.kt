package uab.cs422.projectinlook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddEventViewModel : ViewModel() {

    private val _startTimeText = MutableLiveData<String>().apply {
        value = ""
    }
    val startTimeText: LiveData<String> = _startTimeText

    private val _startDateText = MutableLiveData<String>().apply {
        value = ""
    }
    val startDateText: LiveData<String> = _startDateText

    private val _endTimeText = MutableLiveData<String>().apply {
        value = ""
    }
    val endTimeText: LiveData<String> = _endTimeText

    private val _endDateText = MutableLiveData<String>().apply {
        value = ""
    }
    val endDateText: LiveData<String> = _endDateText

    fun setStartTimeText(text: String) {
        _startTimeText.value = text
    }

    fun setStartDateText(text: String) {
        _startDateText.value = text
    }

    fun setEndTimeText(text: String) {
        _endTimeText.value = text
    }

    fun setEndDateText(text: String) {
        _endDateText.value = text
    }

}