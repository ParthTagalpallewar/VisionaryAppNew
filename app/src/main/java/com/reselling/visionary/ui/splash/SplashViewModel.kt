package com.reselling.visionary.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//private const val TAG = "NewSplashViewModel"
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val splashChannel = Channel<SplashEvents>()
    val splashEventsFlow = splashChannel.receiveAsFlow()

    private val splashLoadingTime = 4 // splash loading time in seconds

    init {
        viewModelScope.launch {
            //sending loading event to splash screen
            splashChannel.send(SplashEvents.LoadingEvent)

            // splash loading
            delay((1000 * splashLoadingTime).toLong())

            /*user in database is not null then navigate to homeScreen*/
            authRepository.getUser().let {
                splashChannel.send(SplashEvents.NavigateToHomeScreen)
            }

            /*If User Log out then send to login Screen*/
            splashChannel.send(SplashEvents.NavigateToLoginScreen)
        }
    }
}


sealed class SplashEvents {
    object NavigateToHomeScreen : SplashEvents()
    object NavigateToLoginScreen : SplashEvents()
    object LoadingEvent : SplashEvents()
}


