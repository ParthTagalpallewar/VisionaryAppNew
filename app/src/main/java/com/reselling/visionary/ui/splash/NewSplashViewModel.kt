package com.reselling.visionary.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.models.dataModels.Constants
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.preferences.UserInfo
import com.reselling.visionary.data.repository.AuthRepository
import com.reselling.visionary.utils.UserLogOutId
import com.reselling.visionary.utils.saveConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NewSplashViewModel"

@HiltViewModel
class NewSplashViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val splashEvents = Channel<SplashEvents>()
    val tasksEvent = splashEvents.receiveAsFlow()

    private val userFromRoomFlow: Flow<UserInfo> = preferencesManager.preferenceFlow


    init {
        getConstants()
    }


    private fun getConstants() = viewModelScope.launch {
        splashEvents.send(SplashEvents.Loading)
        val constantsResponse = authRepository.getConstants()

        when (constantsResponse) {
            is Resource.NoInterException -> {
                splashEvents.send(SplashEvents.InternetProblem)
            }
            is Resource.Failure -> {
                splashEvents.send(SplashEvents.ShowInvalidInputMessage(constantsResponse.errorBody))
            }
            is Resource.Success -> {
                val constants: Constants = constantsResponse.value.constants
                saveConstants(constants.enablePhoneVerification, constants.getProvider)

                //handle navigation
                userFromRoomFlow.collect { user ->
                    /*If User Log out then send to login Screen*/
                    if (user.id == UserLogOutId) {
                        splashEvents.send(SplashEvents.NavigateToLoginScreen)
                    }

                    /*Navigate User to Home Screen*/
                    splashEvents.send((SplashEvents.NavigateToHomeScreen))
                }
            }
        }
    }
}


sealed class SplashEvents {
    object NavigateToHomeScreen : SplashEvents()
    object NavigateToLoginScreen : SplashEvents()
    object Loading : SplashEvents()
    data class ShowInvalidInputMessage(val msg: String) : SplashEvents()
    object InternetProblem : SplashEvents()
}


