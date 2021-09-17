package com.myastrotell.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myastrotell.data.database.entities.UserProfileEntity


@Dao
interface UserProfileDao {

    @Query("SELECT * FROM userprofile WHERE msisdn=:number")
    suspend fun getProfileData(number: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfileData(entity: UserProfileEntity)

    @Query("DELETE FROM userprofile")
    suspend fun clearProfileData()

}