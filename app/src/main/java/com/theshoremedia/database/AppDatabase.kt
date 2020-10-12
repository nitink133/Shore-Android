package com.theshoremedia.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.theshoremedia.AppController
import com.theshoremedia.database.dao.FactCheckHistoryDao
import com.theshoremedia.database.dao.SourcesDao
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.database.entity.NewsSourceModel

@Database(
    entities = [FactCheckHistoryModel::class, NewsSourceModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    RoomConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun factChecksHistoryDao(): FactCheckHistoryDao?
    abstract fun customSourcesDao(): SourcesDao?

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