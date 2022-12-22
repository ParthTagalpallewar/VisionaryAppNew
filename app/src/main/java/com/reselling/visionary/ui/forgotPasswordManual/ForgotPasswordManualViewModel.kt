package com.reselling.visionary.ui.forgotPasswordManual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordManualViewModel @Inject constructor(

        private val authRepository: AuthRepository,
        private val preferencesManager: PreferencesManager
) : ViewModel() {
    var name = ""
        set(value) {
            field = value
        }

    var phoneNumber = ""
        set(value) {
            field = value
        }

    var email =""
        set(value) {
            field = value
        }

    var countryCode = "+91"
        set(value) {
            field = value
        }

    private val _forgotPasswordManualChannel = Channel<ForgotPasswordManualEvents>()
    val forgotPasswordManualEvents = _forgotPasswordManualChannel.receiveAsFlow()

    sealed class ForgotPasswordManualEvents {
        data class ShowInvalidInputMessage(val msg: String) : ForgotPasswordManualEvents()
        data class ObserveForgotPasswordManualResponse(val response: Resource<UserResponseModel>) :
                ForgotPasswordManualEvents()


        object LoadingEvent : ForgotPasswordManualEvents()

    }


    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        _forgotPasswordManualChannel.send(ForgotPasswordManualEvents.ShowInvalidInputMessage(text))
    }

    fun verifyUserBtnClicked() {
        viewModelScope.launch {


            _forgotPasswordManualChannel.send(ForgotPasswordManualEvents.LoadingEvent)

            when {
                (name.isBlank()) -> {
                    showInvalidInputMessage("Please Enter Your Name")
                }
                (phoneNumber.isBlank()) or (phoneNumber.length != 10) -> {
                    showInvalidInputMessage("Please Enter valid Phone Number")
                }
                email.isBlank() -> {
                    showInvalidInputMessage("Please Enter Password")
                }
                else -> {
                    verifyUser()
                }

            }
        }

    }

    private fun verifyUser() = viewModelScope.launch {
        val response = authRepository.forgotPasswordManual(countryCode + phoneNumber, email)
        _forgotPasswordManualChannel.send(ForgotPasswordManualEvents.ObserveForgotPasswordManualResponse(response))
    }

    fun savePhoneNumberInPrefManager(phone: String) = viewModelScope.launch{
        preferencesManager.updateUserPhone(countryCode + phoneNumber)
    }
}