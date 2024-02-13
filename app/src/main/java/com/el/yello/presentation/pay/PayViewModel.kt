package com.el.yello.presentation.pay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.PayInAppModel
import com.example.domain.entity.PayRequestModel
import com.example.domain.entity.PaySubsModel
import com.example.domain.entity.ProfileUserModel
import com.example.domain.repository.PayRepository
import com.example.domain.repository.ProfileRepository
import com.example.ui.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PayViewModel @Inject constructor(
    private val payRepository: PayRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    var currentInAppItem = String()

    private val _postSubsCheckState = MutableStateFlow<UiState<PaySubsModel?>>(UiState.Empty)
    val postSubsCheckState: StateFlow<UiState<PaySubsModel?>> = _postSubsCheckState

    private val _postInAppCheckState = MutableStateFlow<UiState<PayInAppModel?>>(UiState.Empty)
    val postInAppCheckState: StateFlow<UiState<PayInAppModel?>> = _postInAppCheckState

    private val _getUserInfoState = MutableStateFlow<UiState<ProfileUserModel?>>(UiState.Empty)
    val getUserInfoState: StateFlow<UiState<ProfileUserModel?>> = _getUserInfoState

    var ticketCount = 0
        private set

    fun setTicketCount(count: Int) {
        ticketCount = count
    }

    fun addTicketCount(count: Int) {
        ticketCount += count
    }

    fun checkSubsValidToServer(request: PayRequestModel) {
        viewModelScope.launch {
            _postSubsCheckState.value = UiState.Loading
            payRepository.postToCheckSubs(request)
                .onSuccess {
                    _postSubsCheckState.value = UiState.Success(it)
                }
                .onFailure {
                    _postSubsCheckState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun checkInAppValidToServer(request: PayRequestModel) {
        viewModelScope.launch {
            _postInAppCheckState.value = UiState.Loading
            payRepository.postToCheckInApp(request)
                .onSuccess {
                    _postInAppCheckState.value = UiState.Success(it)
                }
                .onFailure {
                    _postInAppCheckState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun getUserDataFromServer() {
        viewModelScope.launch {
            _getUserInfoState.value = UiState.Loading
            profileRepository.getUserData()
                .onSuccess { info ->
                    if (info == null) {
                        _getUserInfoState.value = UiState.Empty
                    } else {
                        _getUserInfoState.value = UiState.Success(info)
                    }
                }
                .onFailure { t ->
                    _getUserInfoState.value = UiState.Failure(t.message.orEmpty())
                }
        }
    }

}
