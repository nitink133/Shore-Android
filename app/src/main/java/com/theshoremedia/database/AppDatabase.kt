package com.theshoremedia.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.theshoremedia.AppController
import com.theshoremedia.database.dao.FactCheckHistoryDio
import com.theshoremedia.database.entity.FactCheckHistoryModel

@Database(entities = [FactCheckHistoryModel::class], version = 1, exportSchema = false)
@TypeConverters(
    RoomConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun factChecksHistoryDao(): FactCheckHistoryDio?

    companion object {
        private var databaseInstance: AppDatabase? = null

        // allow queries on the main thread.
        // Don't do this on a real app! See PersistenceBasicSample for an example.
        val appDatabase: AppDatabase
            get() {
                if (databaseInstance == null) {
                    databaseInstance = Room.databaseBuilder(
                        AppController.getInstance(),
                        AppDatabase::class.java,
                        "TheShore-database"
                    ) // allow queries on the main thread.
                        // Don't do this on a real app! See PersistenceBasicSample for an example.
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return databaseInstance!!
            }
    }
}