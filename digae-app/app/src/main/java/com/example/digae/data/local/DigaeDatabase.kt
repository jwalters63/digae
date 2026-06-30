package com.example.digae.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.digae.data.local.dao.UserProfileDao
import com.example.digae.data.local.entity.UserProfileEntity

@Database(entities = [UserProfileEntity::class], version = 1, exportSchema = false)
abstract class DigaeDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: DigaeDatabase? = null

        fun getDatabase(context: Context): DigaeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DigaeDatabase::class.java,
                    "digae_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
