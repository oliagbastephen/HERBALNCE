package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        FoodLogEntity::class,
        WaterLogEntity::class,
        WeightLogEntity::class,
        MealPlanEntity::class,
        RecipeEntity::class,
        ShoppingItemEntity::class,
        DailyCheckInEntity::class,
        AppNotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HerbalanceDatabase : RoomDatabase() {
    abstract fun herbalanceDao(): HerbalanceDao

    companion object {
        @Volatile
        private var INSTANCE: HerbalanceDatabase? = null

        fun getDatabase(context: Context): HerbalanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HerbalanceDatabase::class.java,
                    "herbalance_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
