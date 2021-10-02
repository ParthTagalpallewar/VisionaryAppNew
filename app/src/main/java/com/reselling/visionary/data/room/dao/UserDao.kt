package com.reselling.visionary.data.room.dao

import androidx.room.*
import com.reselling.visionary.data.models.userModel.AutoGenerateUserId
import com.reselling.visionary.data.models.userModel.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("Select * FROM `USER-TABLE` WHERE Uid = :id")
    fun getUserFromRoom(id: Int = AutoGenerateUserId): User

    @Query("Select * FROM `USER-TABLE` WHERE Uid = :id")
    fun getUserFromRoomFLow(id: Int = AutoGenerateUserId): Flow<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User):Long

    @Update
    fun updateUser(user: User): Int


    @Query("DELETE  FROM `user-table` Where Uid = :id")
    fun logOut(id: Int = AutoGenerateUserId)
}