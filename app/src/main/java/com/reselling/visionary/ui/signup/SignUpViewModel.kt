package com.reselling.visionary.ui.signup

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

private const val TAG = "SignUpViewModel"
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _signUpTaskEventChannel = Channel<SignUpFragmentEvents>()
    val signUpEvent = _signUpTaskEventChannel.receiveAsFlow()

    // make signup call in auth repository
    fun signUpBtnClicked(
        name: String,
        phoneNumber: String,
        password: String,
        countryCode: String,
        email: String
    ) = viewModelScope.launch {

        _signUpTaskEventChannel.send(SignUpFragmentEvents.LoadingEvent)

        val validationResult = validateUserData(name, phoneNumber, password, email)

        if (validationResult) {
            // sending signup request to serer
            val response = authRepository.signUp(
                phone = countryCode + phoneNumber,
                password = password,
                name = name,
                email = email
            )

            when (response) {
                is Resource.Failure -> {
                    showInvalidInputMessage(response.errorBody)
                }

                is Resource.Success -> {

                    //adding user to room
                    authRepository.insertUser(response.value.user).also {

                        //moving to Home fragment
                        _signUpTaskEventChannel.send(SignUpFragmentEvents.NavigateToHomeFragment)


                    }


                }

                else -> {
                    Log.e(TAG, "signUpBtnClicked: Else is Called after receiving Signup Response")
                }
            }
        }

    }


    /*If every field is correct then it will return true else false*/
    private fun validateUserData(
        name: String,
        phoneNumber: String,
        password: String,
        email: String
    ): Boolean {
        when {
            (name.isBlank()) -> {
                showInvalidInputMessage("Please Enter Your Name")
            }
            (phoneNumber.isBlank()) or (phoneNumber.length != 10) -> {
                showInvalidInputMessage("Please Enter valid Phone Number")
            }
            password.isBlank() -> {
                showInvalidInputMessage("Please Enter Password")
            }
            password.length < 7 -> {
                showInvalidInputMessage("Your password is Weak")
            }
            email.isBlank() -> {
                showInvalidInputMessage("Please Enter Email")
            }
            else -> {

                return true

            }
        }
        return false
    }


    /*Sends the error message in fragment*/
    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        _signUpTaskEventChannel.send(SignUpFragmentEvents.ShowInvalidInputMessage(text))
    }


    sealed class SignUpFragmentEvents {
        data class ShowInvalidInputMessage(val msg: String) : SignUpFragmentEvents()
        object LoadingEvent : SignUpFragmentEvents()
        object NavigateToHomeFragment : SignUpFragmentEvents()
    }

}
