package com.myastrotell.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "selectedLanguages",
    indices = [Index(value = ["id"], unique = true)]
)
class SelectedLanguagesEntity {
    @PrimaryKey(autoGenerate = true)
    var _ID: Int = 0

    var id: Int = 0

    var key: String? = null

    var value: String? = null

}