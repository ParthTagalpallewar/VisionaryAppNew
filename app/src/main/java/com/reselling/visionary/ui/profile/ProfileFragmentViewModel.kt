package com.reselling.visionary.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.models.userModel.User
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.repository.AuthRepository
import com.reselling.visionary.ui.splash.NewSplashViewModel
import com.reselling.visionary.utils.UserLogOutId
import com.reselling.visionary.utils.UserNoLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
        private val userRepo: AuthRepository,
        private val preferencesManager: PreferencesManager
) : ViewModel() {


    val userData = MutableLiveData<User>()


    init {
        viewModelScope.launch {
            userRepo.getUserFlow().collect {
                if (it != null) {
                    userData.postValue(it)
                }
            }

        }
    }

    fun logOutUser() = viewModelScope.launch {

        preferencesManager.updateUserId(UserLogOutId)
        preferencesManager.updateUserLoc(UserNoLocation)

        userRepo.logoutUser()
        _profileFragmentChannel.send(ProfileFragmentEvents.NavigateToLoginFragment)
    }

    private val _profileFragmentChannel = Channel<ProfileFragmentEvents>()
    val profileEvents = _profileFragmentChannel.receiveAsFlow()

    sealed class ProfileFragmentEvents {

        object NavigateToLoginFragment : ProfileFragmentEvents()
    }

}