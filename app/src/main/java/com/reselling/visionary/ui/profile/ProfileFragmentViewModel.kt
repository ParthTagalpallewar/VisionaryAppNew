package com.reselling.visionary.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
        private val userRepo: AuthRepository,
        private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _profileFragmentChannel = Channel<ProfileFragmentEvents>()
    val profileEvents = _profileFragmentChannel.receiveAsFlow()

    val userData = userRepo.getUserFlow()

    // delete user form room database and send user to login screen
    fun logOutUser() = viewModelScope.launch {
        userRepo.logoutUser()
        _profileFragmentChannel.send(ProfileFragmentEvents.NavigateToLoginFragment)
    }


    sealed class ProfileFragmentEvents {
        object NavigateToLoginFragment : ProfileFragmentEvents()
    }

}