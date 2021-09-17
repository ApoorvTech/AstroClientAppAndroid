package com.myastrotell.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "cart",
    indices = [Index(value = ["productId"], unique = true)]
)
class CartEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var productId: String? = null

    var productName: String? = null

    var productDescription: String? = null

    var productThumb: String? = null

    var productPrice: String? = null

    var unit: String? = null

    var quantity: Int = 1

    var consultantName: String? = null

    var category: String? = null

    var total : Double? = null

}