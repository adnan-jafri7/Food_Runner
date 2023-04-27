package com.adnan.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

    @Entity(tableName="orders")
    data class OrderEntity(
        @PrimaryKey val resId:String,
        @ColumnInfo(name="food_items") var foodItems:String
    )