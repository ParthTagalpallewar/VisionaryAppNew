package com.reselling.visionary.ui.forgotPassword.updatePassword

import android.util.Log
import androidx.lifecycle.*
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
        private val state: SavedStateHandle,
        private val authRepository: AuthRepository,
        private val preferencesManager: PreferencesManager,

        ) : ViewModel() {

    fun updatePassword() {
        when {
            newPassword.isBlank() -> {
                showInvalidInputMessage("Please Fill all Details")
            }

            confirmPassword.isBlank() -> {
                showInvalidInputMessage("Please Fill all Details")
            }

            newPassword != confirmPassword -> {
                showInvalidInputMessage("Password Does not match! ")
            }

            else -> {
                viewModelScope.launch {

                    _changePasswordChannel.send(UpdatePasswordEvents.LoadingEvent)
                    preferencesManager.prefFlowPhoneNumber.collect { phoneNumber ->
                        _changePasswordChannel.send(UpdatePasswordEvents.UpdatePasswordResponse(authRepository.changeUserPassword(phoneNumber!!, newPassword)))
                    }

                }


            }
        }
    }

    fun handleUpdateResponse(it: Resource<UserResponseModel>?) = viewModelScope.launch {
        _changePasswordChannel.send(UpdatePasswordEvents.LoadingEvent)
        when (it) {
            is Resource.Success -> {
               // Log.e("success", "every this is fine")
                try {
                    _changePasswordChannel.send(UpdatePasswordEvents.NavigateToSuccessFragment)
                } catch (e: Exception) {
                    Log.e("success - erorr", e.toString())
                }
            }
            is Resource.Failure -> {
                showInvalidInputMessage(it.errorBody)
            }

            is Resource.NoInterException -> {
                _changePasswordChannel.send(UpdatePasswordEvents.InternetProblem)
            }

        }
    }

    sealed class UpdatePasswordEvents {
        data class ShowInvalidInputMessage(val msg: String) : UpdatePasswordEvents()
        data class UpdatePasswordResponse(val response: Resource<UserResponseModel>) : UpdatePasswordEvents()
        object LoadingEvent : UpdatePasswordEvents()
        object InternetProblem : UpdatePasswordEvents()
        object NavigateToSuccessFragment : UpdatePasswordEvents()
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        _changePasswordChannel.send(UpdatePasswordEvents.ShowInvalidInputMessage(text))
    }

    private val _changePasswordChannel = Channel<UpdatePasswordEvents>()
    val changePasswordEvents = _changePasswordChannel.receiveAsFlow()


    val phoneNumber = state.get<String>("phone_number")

    var newPassword = state.get<String>("newPassword") ?: ""
        set(value) {
            field = value
            state.set("newPassword", value)
        }

    var confirmPassword = state.get<String>("confirmPassword") ?: ""
        set(value) {
            field = value
            state.set("confirmPassword", value)
        }
}

/*
* update password called form Fragment
* check password null and same
* getting user phone number
* putting that phone number in MutualLiveData
* observing updatePasswordResponse form Fragment
* and sending Data to HandleUpdateResponse
*
* */