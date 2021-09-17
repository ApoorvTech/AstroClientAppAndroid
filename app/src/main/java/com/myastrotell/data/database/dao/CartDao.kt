package com.myastrotell.data.database.dao

import androidx.room.*
import com.myastrotell.data.database.entities.CartEntity


@Dao
interface CartDao {

    @Query("SELECT * FROM cart")
    suspend fun getCartData(): List<CartEntity>


    @Query("SELECT * FROM cart WHERE productId =:productId")
    suspend fun getItemById(productId: String): CartEntity?


    @Query("SELECT COUNT(productId) FROM cart")
    suspend fun getTotalItemsCount(): Int


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntoCart(entity: CartEntity)


    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCartItem(entity: CartEntity)


    @Query("UPDATE cart SET quantity =:quantity WHERE productId =:productId")
    suspend fun updateQuantity(productId: String, quantity: Int)


    @Query("UPDATE cart SET consultantName =:consultantName WHERE productId =:productId")
    suspend fun updateConsultantName(productId: String, consultantName: String)


    @Query("DELETE FROM cart WHERE productId =:productId")
    suspend fun deleteItemFromCart(productId: String)


    @Query("DELETE FROM cart")
    suspend fun clearCart()

}