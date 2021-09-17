package com.myastrotell.data.database.dao

import androidx.room.*
import com.myastrotell.data.database.entities.IntakeFormEntity

@Dao
interface IntakeFormDao {

    @Query("SELECT * FROM intakeform WHERE mobileNumber=:number")
    suspend fun getIntakeFormData(number: String): IntakeFormEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntoIntakeForm(entity: IntakeFormEntity)

    @Query("DELETE FROM intakeform")
    suspend fun clearIntakeFormData()

}