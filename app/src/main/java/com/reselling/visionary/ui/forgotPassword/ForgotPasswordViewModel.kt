package com.reselling.visionary.ui.forgotPassword

import android.app.Activity
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.reselling.visionary.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val mFirebaseAuth: FirebaseAuth,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private lateinit var _activity: Activity
    private lateinit var _verificationCode: String
    private lateinit var _resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var _stateChangeCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    //called form fragment
    fun nextBtnClicked(activity: Activity) {
        viewModelScope.launch {
            _activity = activity

            _changePasswordChannel.send(ChangePasswordEvents.LoadingEvent)

            when {
                (phoneNumber.isBlank()) or (phoneNumber.length != 10) -> {
                    showInvalidInputMessage("Please Enter valid Phone Number")
                }
                else -> {

                    viewModelScope.launch {
                        _changePasswordChannel.send(ChangePasswordEvents.LoadingEvent)
                    }

                    sendVerificationCode()

                }

            }
        }

    }

    //send Verification code
    private fun sendVerificationCode() {

        _stateChangeCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credentail: PhoneAuthCredential) {
                mFirebaseAuth.signInWithCredential(credentail).addOnCompleteListener {
                    if (it.isSuccessful) {

                        viewModelScope.launch {
                            preferencesManager.updateUserPhone(countryCode+phoneNumber)
                            _changePasswordChannel.send(ChangePasswordEvents.NavigateToUpdatePasswordFragment)
                        }

                    } else {

                    }
                }

            }

            override fun onVerificationFailed(exception: FirebaseException) {
                showInvalidInputMessage(
                    exception.localizedMessage ?: exception.cause.toString()
                )
            }

            override fun onCodeSent(
                verificationId: String,
                resendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                viewModelScope.launch {
                    super.onCodeSent(verificationId, resendingToken)

                    _verificationCode = verificationId
                    _resendToken = resendingToken

                    //navigateToVerifyCodeFragment
                    preferencesManager.updateUserPhone(countryCode+phoneNumber)
                    _changePasswordChannel.send(ChangePasswordEvents.NavigateToVerifyCodeFragment)
                }
            }


        }


        val phoneAuthOptions = PhoneAuthOptions.newBuilder(mFirebaseAuth)
            .apply {
                setPhoneNumber(countryCode + phoneNumber)
                setTimeout(60L, TimeUnit.SECONDS)
                setActivity(_activity)
                setCallbacks(_stateChangeCallback)
            }.build()

        //this will send code
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)

    }

    //verify opt
    fun verifyCode() {

        when {
            (otp.isBlank()) or (otp.length != 6) -> {
                showInvalidInputMessage("Please Enter valid Pin Number")
            }

            else -> viewModelScope.launch {
                _changePasswordChannel.send(ChangePasswordEvents.LoadingEvent)


                val credential = PhoneAuthProvider.getCredential(_verificationCode, otp)
                mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        viewModelScope.launch {
                            preferencesManager.updateUserPhone(countryCode+phoneNumber)
                            _changePasswordChannel.send(ChangePasswordEvents.NavigateToUpdatePasswordFragment)
                        }

                    } else {
                        showInvalidInputMessage(
                            it.exception!!.localizedMessage
                                ?: "Some error happen while authentication \n Please Try again! "
                        )
                    }
                }
            }
        }
    }


    var phoneNumber = state.get<String>("phoneNumber") ?: ""
        set(value) {
            field = value
            state.set("phoneNumber", value)
        }

    var countryCode = state.get<String>("countryCode") ?: "+91"
        set(value) {
            field = value
            state.set("countryCode", value)
        }

    var otp = state.get<String>("otp") ?: ""
        set(value) {
            field = value
            state.set("otp", value)
        }

    sealed class ChangePasswordEvents {
        data class ShowInvalidInputMessage(val msg: String) : ChangePasswordEvents()
        object LoadingEvent : ChangePasswordEvents()
        object InternetProblem : ChangePasswordEvents()
        object NavigateToUpdatePasswordFragment : ChangePasswordEvents()
        object NavigateToVerifyCodeFragment : ChangePasswordEvents()


    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        Log.wtf("LoginFragment", text)
        _changePasswordChannel.send(ChangePasswordEvents.ShowInvalidInputMessage(text))
    }


    private val _changePasswordChannel = Channel<ChangePasswordEvents>()
    val changePasswordEvents = _changePasswordChannel.receiveAsFlow()
}
