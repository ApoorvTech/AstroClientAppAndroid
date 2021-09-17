package com.myastrotell.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.myastrotell.data.database.converters.ListConverters
import com.myastrotell.data.database.dao.CartDao
import com.myastrotell.data.database.dao.IntakeFormDao
import com.myastrotell.data.database.dao.SelectedLanguageDao
import com.myastrotell.data.database.dao.UserProfileDao
import com.myastrotell.data.database.entities.CartEntity
import com.myastrotell.data.database.entities.IntakeFormEntity
import com.myastrotell.data.database.entities.SelectedLanguagesEntity
import com.myastrotell.data.database.entities.UserProfileEntity


@Database(
    entities = [CartEntity::class, IntakeFormEntity::class, SelectedLanguagesEntity::class, UserProfileEntity::class],
    version = 14,
    exportSchema = false
)
@TypeConverters(ListConverters::class)
abstract class AppDatabase : RoomDatabase() {




    companion object {
        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE userprofile ADD COLUMN email TEXT")
            }
        }
        private var mInstance: AppDatabase? = null

        private const val DATABASE_NAME = "astro_user.db"

        fun getInstance(context: Context): AppDatabase {
            if (mInstance == null) {
                synchronized(this) {
                    if (mInstance == null) {
                        mInstance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            DATABASE_NAME
                        ).addMigrations(MIGRATION_13_14).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return mInstance!!
        }

    }




    abstract fun cartDao(): CartDao

    abstract fun intakeFormDao(): IntakeFormDao

    abstract fun selectedLanguageDao(): SelectedLanguageDao

    abstract fun userProfileDao(): UserProfileDao

}