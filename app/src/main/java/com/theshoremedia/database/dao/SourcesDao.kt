package com.theshoremedia.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.theshoremedia.database.entity.NewsSourceModel

@Dao
interface SourcesDao {
    @get:Query("SELECT * FROM customNewsSources")
    val all: LiveData<List<NewsSourceModel>?>


    @get:Query("SELECT * FROM customNewsSources")
    val getAllItems: List<NewsSourceModel>?


    @Query("SELECT * FROM customNewsSources Where _isShoreSource = (:isShoreSources)")
    fun getCustomSources(isShoreSources: Boolean = false): List<NewsSourceModel>?


    @Query("SELECT * FROM customNewsSources Where _isShoreSource = (:isShoreSources)")
    fun getShoreSources(isShoreSources: Boolean = true): List<NewsSourceModel>?

    @Query("SELECT * FROM customNewsSources Where _isShoreSource = (:isShoreSources)")
    fun getCustomSourcesLive(isShoreSources: Boolean = false): LiveData<List<NewsSourceModel>?>


    @Query("SELECT * FROM customNewsSources Where _isShoreSource = (:isShoreSources)")
    fun getShoreSourcesLive(isShoreSources: Boolean = true): LiveData<List<NewsSourceModel>?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(newsSourceModels: List<NewsSourceModel?>?): Array<Long?>?

    @Delete
    fun delete(newsSourceModels: List<NewsSourceModel>?)

    @Query("DELETE FROM customNewsSources")
    fun deleteAll()

    @Query("Select * From customNewsSources Where _website LIKE '%' + (:sourceLink) + '%'")
    fun getSourceInfo(sourceLink: String): NewsSourceModel?


    @Update
    fun update(newsSourceModels: NewsSourceModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNew(newsSourceModels: NewsSourceModel)

}