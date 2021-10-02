package com.reselling.visionary.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.models.userModel.UserResponseModel
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.repository.AuthRepository
import com.reselling.visionary.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {


    var phoneNumber = ""
        set(value) {
            field = value
        }

    var password = ""
        set(value) {
            field = value
        }

    var countryCode = "+91"
        set(value) {
            field = value
        }


    private val loginTaskEventChannel = Channel<LoginFragmentEvent>()
    val loginEvent = loginTaskEventChannel.receiveAsFlow()


    fun signInBtnClicked() = viewModelScope.launch {


        when {
            phoneNumber.isBlank() -> {
                showInvalidInputMessage("Please Enter Phone Number")
            }
            password.isBlank() -> {
                showInvalidInputMessage("Please Enter Password")
            }

            else -> {

                loginTaskEventChannel.send(LoginFragmentEvent.LoadingEvent)
                loginTaskEventChannel.send(
                    LoginFragmentEvent.ObserveLoginResponse(
                        authRepository.login(
                            countryCode + phoneNumber,
                            password
                        )
                    )
                )
            }
        }
    }

    fun signUpTextViewClick() = viewModelScope.launch {
        loginTaskEventChannel.send(LoginFragmentEvent.NavigateToSignUpFragment)
    }

    //this is called when we got response from sever
    fun handelLoginResponse(resorce: Resource<UserResponseModel>?) = viewModelScope.launch {
        when (resorce) {
            //if there is no internet
            is Resource.NoInterException ->
                //send channel to fragment
                loginTaskEventChannel.send(
                    LoginFragmentEvent.InternetProblem
                )

            is Resource.Failure -> {
                loginTaskEventChannel.send(LoginFragmentEvent.ShowInvalidInputMessage("Error : ${resorce.errorBody}"))
            }


            is Resource.Success -> {
                withContext(Dispatchers.IO) {
                    resorce.value.user.also { user ->
                        //update location and id in pref Manager
                        preferencesManager.updateUserIdAndLocation(user.id, user.location)

                        //update user in room
                        authRepository.insertUser(resorce.value.user).also {

                            loginTaskEventChannel.send(LoginFragmentEvent.NavigateToMainFragment)

                            deleteAllValues()
                        }
                    }

                }
            }

        }
    }

    private fun deleteAllValues() {
        phoneNumber = ""
        password = ""
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        loginTaskEventChannel.send(LoginFragmentEvent.ShowInvalidInputMessage(text))
    }

    fun forgotPasswordButttonClicked() = viewModelScope.launch {
        if (Constants.ENABLE_PHONE_VERIFICATION) {
            loginTaskEventChannel.send(LoginFragmentEvent.NavigateToForgetPasswordFragment)
        } else {
            loginTaskEventChannel.send(LoginFragmentEvent.NavigateToForgetPasswordManualFragment)
        }

    }

    sealed class LoginFragmentEvent {
        data class ShowInvalidInputMessage(val msg: String) : LoginFragmentEvent()
        data class ObserveLoginResponse(val response: Resource<UserResponseModel>) :
            LoginFragmentEvent()

        object InternetProblem : LoginFragmentEvent()
        object NavigateToMainFragment : LoginFragmentEvent()
        object NavigateToSignUpFragment : LoginFragmentEvent()
        object NavigateToForgetPasswordFragment : LoginFragmentEvent()
        object NavigateToForgetPasswordManualFragment : LoginFragmentEvent()
        object LoadingEvent : LoginFragmentEvent()

    }

}

/*
* 1. loginScreen ->  signInBtnClick
* 2. making login req call and updating value in _loginResponse
* 3. observing loginResponse form loginScreen
* 4. when receive login response call method handelLoginRespone
*
* */