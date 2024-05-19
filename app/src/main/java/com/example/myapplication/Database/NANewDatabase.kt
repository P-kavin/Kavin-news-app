package com.example.myapplication.Database

import android.content.Context
import androidx.room.*
import com.example.myapplication.DatasAndValues.NANewsListDataItem
  

@Database(
    entities = [NANewsListDataItem::class],
    version = 1,
    exportSchema = false
)
abstract class NANewDatabase : RoomDatabase() {
    abstract fun dbDao(): NANewsListDao

    companion object {
        @Volatile
        private var INSTANCE: NANewDatabase? = null
        fun getDatabase(context: Context): NANewDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): NANewDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                NANewDatabase::class.java,
                "newsList_database"
            ).fallbackToDestructiveMigration()
                .build()
        }

    }
}