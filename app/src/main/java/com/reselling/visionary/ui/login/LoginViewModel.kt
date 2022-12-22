package com.reselling.visionary.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    private val loginTaskEventChannel = Channel<LoginFragmentEvent>()
    val loginEvent = loginTaskEventChannel.receiveAsFlow()


    fun signInBtnClicked(phoneNumber: String, countryCode: String, password: String) =
        viewModelScope.launch {

            val validationResponse =
                validateUserInformation(phoneNumber = phoneNumber, password = password)

            if (validationResponse) {

                loginTaskEventChannel.send(LoginFragmentEvent.LoadingEvent)

                /*Send SingUp Request to Server*/
                val signUpResponse = authRepository.login(
                    phone = countryCode + phoneNumber,
                    password = password
                )

                when (signUpResponse) {

                    is Resource.Failure -> {
                        showInvalidInputMessage(signUpResponse.errorBody)
                    }

                    is Resource.Success -> {
                        //update user in room
                        authRepository.insertUser(signUpResponse.value)

                        //move to MainFragment
                        loginTaskEventChannel.send(LoginFragmentEvent.NavigateToMainFragment)
                    }

                    else -> {
                        Log.e(TAG, "signInBtnClicked: Else of SignIn Response")
                    }

                }

            }

        }

    private fun validateUserInformation(
        phoneNumber: String,
        password: String,
    ): Boolean {
        when {
            phoneNumber.isBlank() -> {
                showInvalidInputMessage("Please Enter Phone Number")
            }
            password.isBlank() -> {
                showInvalidInputMessage("Please Enter Password")
            }

            else -> {
                return true
            }
        }
        return false
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        loginTaskEventChannel.send(LoginFragmentEvent.ShowInvalidInputMessage(text))
    }

    sealed class LoginFragmentEvent {
        data class ShowInvalidInputMessage(val msg: String) : LoginFragmentEvent()

        object NavigateToMainFragment : LoginFragmentEvent()

        object LoadingEvent : LoginFragmentEvent()

    }

}
