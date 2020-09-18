package com.theshoremedia.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.theshoremedia.database.entity.NewsSourceModel

@Dao
interface CustomSourcesDao {
    @get:Query("SELECT * FROM customNewsSources")
    val all: LiveData<List<NewsSourceModel>?>


    @get:Query("SELECT * FROM customNewsSources")
    val getAllItems: List<NewsSourceModel>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(newsSourceModels: List<NewsSourceModel?>?): Array<Long?>?

    @Delete
    fun delete(newsSourceModels: List<NewsSourceModel>?)

    @Query("DELETE FROM customNewsSources")
    fun deleteAll()


    @Update
    fun update(newsSourceModels: NewsSourceModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNew(newsSourceModels: NewsSourceModel)

}