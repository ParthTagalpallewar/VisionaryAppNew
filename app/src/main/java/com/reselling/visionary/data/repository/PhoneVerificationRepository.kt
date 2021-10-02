package com.reselling.visionary.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.reselling.visionary.data.network.networkResponseType.Resource
import javax.inject.Inject

class PhoneVerificationRepository @Inject constructor(
    private val mFirebaseAuth:FirebaseAuth
){





}