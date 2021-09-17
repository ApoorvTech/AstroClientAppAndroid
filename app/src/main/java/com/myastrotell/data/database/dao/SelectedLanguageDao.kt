package com.myastrotell.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myastrotell.data.database.entities.SelectedLanguagesEntity


@Dao
interface SelectedLanguageDao {

    @Query("SELECT * FROM selectedLanguages")
    suspend fun getSelectedLanguages(): List<SelectedLanguagesEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguage(entity: SelectedLanguagesEntity)


    @Query("DELETE FROM selectedLanguages")
    suspend fun clearLanguages()

}