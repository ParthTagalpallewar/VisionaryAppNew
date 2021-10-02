package com.reselling.visionary.ui.signup

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.models.userModel.SignUpModel
import com.reselling.visionary.data.models.userModel.UserResponseModel
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.repository.AuthRepository
import com.reselling.visionary.utils.Constants
import com.reselling.visionary.utils.UserNoLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "SignUpViewModel"

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {


    var name =  ""
        set(value) {
            field = value
        }

    var phoneNumber =  ""
        set(value) {
            field = value
        }

    var password =  ""
        set(value) {
            field = value
        }

    var countryCode = "+91"
        set(value) {
            field = value
        }

    var email =  ""
        set(value) {
            field = value
        }


    //called form fragment
    fun signUpBtnClicked() {
        viewModelScope.launch {

            _signUpTaskEventChannel.send(SignUpFragmentEvents.LoadingEvent)

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
                    showInvalidInputMessage("Please Enter Password")
                }
                else -> {
                    if (Constants.ENABLE_PHONE_VERIFICATION) {
                        //todo add twilio service here

                    } else {
                        signUpUser()
                    }
                }

            }
        }

    }


    //send signUp req to network
    private fun signUpUser() = viewModelScope.launch {
        _signUpTaskEventChannel.send(SignUpFragmentEvents.LoadingEvent)

        _signUpTaskEventChannel.send(
            SignUpFragmentEvents.ObserveSignUpResponse(
                authRepository.signUp(SignUpModel(name, countryCode + phoneNumber, email, password))
            )
        )

    }


    fun handelSignUpResponse(response: Resource<UserResponseModel>?) = viewModelScope.launch {
        response.apply {
            when (this) {
                is Resource.NoInterException -> {
                    viewModelScope.launch {
                        _signUpTaskEventChannel.send(SignUpFragmentEvents.InternetProblem)
                    }
                }

                is Resource.Failure -> {
                    showInvalidInputMessage("Error : $errorBody")
                }

                is Resource.Success -> {
                    withContext(Dispatchers.IO) {

                        deleteALlValues()

                        //adding data form response to dataStore
                        preferencesManager.updateUserIdAndLocation(
                            value.user.id,
                            if (value.user.location == "NO") UserNoLocation else value.user.location
                        ).also {

                            //adding user to room
                            authRepository.insertUser(value.user).also {

                                //moving to location fragment
                                _signUpTaskEventChannel.send(SignUpFragmentEvents.NavigateToHomeFragment)


                            }
                        }

                    }


                }

            }
        }
    }

    private fun deleteALlValues() {
        phoneNumber = ""
        password = ""
        name = ""
        email = ""
    }

    private val _signUpTaskEventChannel = Channel<SignUpFragmentEvents>()
    val signUpEvent = _signUpTaskEventChannel.receiveAsFlow()

    sealed class SignUpFragmentEvents {
        data class ShowInvalidInputMessage(val msg: String) : SignUpFragmentEvents()
        data class ObserveSignUpResponse(val response: Resource<UserResponseModel>) :
            SignUpFragmentEvents()


        object InternetProblem : SignUpFragmentEvents()

        object LoadingEvent : SignUpFragmentEvents()
        object NavigateToVerifyCodeFragment : SignUpFragmentEvents()
        object NavigateToHomeFragment : SignUpFragmentEvents()
        object HideResendBtn : SignUpFragmentEvents()
    }


    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        _signUpTaskEventChannel.send(SignUpFragmentEvents.ShowInvalidInputMessage(text))
    }


}
