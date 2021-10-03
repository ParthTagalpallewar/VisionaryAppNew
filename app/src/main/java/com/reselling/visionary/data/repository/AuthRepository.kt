package com.reselling.visionary.data.repository

import com.reselling.visionary.data.network.apis.AuthApi
import com.reselling.visionary.data.network.apis.DataApi
import com.reselling.visionary.data.network.networkResponseType.MySafeApiRequest
import com.reselling.visionary.data.models.userModel.User
import com.reselling.visionary.data.models.userModel.UserResponseModel
import com.reselling.visionary.data.network.apis.UserOperationsApi
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.room.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

import javax.inject.Inject

class AuthRepository @Inject constructor(
        private val authApi: AuthApi,
        private val dataApi: DataApi,
        private val userDao: UserDao,
        private val userApi: UserOperationsApi
) : MySafeApiRequest() {

    suspend fun login(phone: String, password: String) =
            apiRequest { authApi.login(phone, password) }

    suspend fun insertUser(user: User) = withContext(Dispatchers.IO) { userDao.insertUser(user) }

    //In dataBase
    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) { userDao.updateUser(user) }

    suspend fun signUp(name:String, email:String, phone:String, password: String) = apiRequest {
        authApi.userSignUp(
                phone = phone,
                password = password,
                name = name,
                email = email
        )
    }

    suspend fun getUser(): User {
        return withContext(Dispatchers.IO) {
            userDao.getUserFromRoom()
        }
    }

    fun getUserFlow(): Flow<User> {

        return userDao.getUserFromRoomFLow()

    }

    //In Api
    suspend fun changeUserPassword(
            userId: String,
            newPassword: String
    ): Resource<UserResponseModel> = apiRequest { userApi.updatePassword(userId, newPassword) }


    suspend fun forgotPasswordManual(
            phone: String,
            email: String
    ): Resource<UserResponseModel> = apiRequest { userApi.forgotPasswordManual(phone, email) }


    suspend fun getConstants() = apiRequest {
        dataApi.getConstants()
    }

    suspend fun logoutUser() {
        withContext(Dispatchers.IO) {
            userDao.logOut()
        }

    }


}