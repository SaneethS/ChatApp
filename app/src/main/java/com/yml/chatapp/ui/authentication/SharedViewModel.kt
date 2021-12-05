package com.yml.chatapp.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    private val _goToHomeActivityStatus = MutableLiveData<Boolean>()
    val goToHomeActivityStatus = _goToHomeActivityStatus as LiveData<Boolean>

    fun setGoToHomeActivity(status: Boolean) {
        _goToHomeActivityStatus.value = status
    }
}