package com.yello.presentation.main.yello

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.type.YelloState
import com.example.domain.entity.type.YelloState.Lock
import com.example.domain.entity.type.YelloState.Valid
import com.example.domain.entity.type.YelloState.Wait
import com.example.domain.repository.VoteRepository
import com.example.ui.view.UiState
import com.example.ui.view.UiState.Failure
import com.example.ui.view.UiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class YelloViewModel @Inject constructor(
    private val voteRepository: VoteRepository,
) : ViewModel() {
    private val _yelloState = MutableLiveData<UiState<YelloState>>()
    val yelloState: LiveData<UiState<YelloState>>
        get() = _yelloState

    private val _leftTime = MutableLiveData(SEC_MAX_LOCK_TIME)
    val leftTime: LiveData<Int> = _leftTime

    init {
        getVoteState()
    }

    fun decreaseTime() {
        viewModelScope.launch {
            leftTime.value ?: return@launch
            while (requireNotNull(leftTime.value) > 0) {
                delay(1000L)
                if (requireNotNull(leftTime.value) <= 0) return@launch
                _leftTime.value = leftTime.value?.minus(1)
            }
        }

        // TODO: Yello State 바꾸기
    }

    fun getVoteState() {
        viewModelScope.launch {
            voteRepository.getVoteAvailable()
                .onSuccess { voteState ->
                    if (voteState.isStart) {
                        _yelloState.value = Success(Valid(voteState.point))
                        return@launch
                    }

                    _yelloState.value = Success(Wait(voteState.leftTime))
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("GET VOTE STATE FAILURE : $t")
                        when (t.code()) {
                            CODE_NO_FRIEND -> _yelloState.value = Success(Lock)
                            else -> _yelloState.value = Failure(t.message())
                        }
                    }
                }
        }
    }

    companion object {
        private const val SEC_MAX_LOCK_TIME = 24

        private const val CODE_NO_FRIEND = 404
    }
}
