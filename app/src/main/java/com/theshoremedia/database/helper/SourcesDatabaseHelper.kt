package com.theshoremedia.database.helper

import android.content.Context
import android.os.Handler
import androidx.lifecycle.LifecycleOwner
import com.theshoremedia.database.AppDatabase
import com.theshoremedia.database.entity.NewsSourceModel
import com.theshoremedia.utils.ObjectUtils

/**
 * @author- Nitin Khanna
 * @date -
 */
class SourcesDatabaseHelper {
    companion object {
        private var databaseManager: SourcesDatabaseHelper? = null
        private var handler: Handler? = null
        val instance: SourcesDatabaseHelper?
            get() {
                if (databaseManager == null) {
                    databaseManager = SourcesDatabaseHelper()
                    handler = Handler()
                }
                return databaseManager
            }
    }


    fun getAllSources(
        owner: LifecycleOwner,
        callBack: ((items: List<NewsSourceModel>) -> Unit)
    ) {
        handler?.post {

            AppDatabase.appDatabase.customSourcesDao()!!.all.observe(
                owner,
                androidx.lifecycle.Observer {
                    callBack.invoke(it ?: arrayListOf())
                })
        }
    }

    fun getCustomSourcesLive(
        owner: LifecycleOwner,
        callBack: ((items: List<NewsSourceModel>) -> Unit)
    ) {
        handler?.post {

            AppDatabase.appDatabase.customSourcesDao()!!.getCustomSourcesLive().observe(
                owner,
                androidx.lifecycle.Observer {
                    callBack.invoke(it ?: arrayListOf())
                })
        }
    }

    fun getShoreSourcesLive(
        owner: LifecycleOwner,
        callBack: ((items: List<NewsSourceModel>) -> Unit)
    ) {
        handler?.post {

            AppDatabase.appDatabase.customSourcesDao()!!.getShoreSourcesLive().observe(
                owner,
                androidx.lifecycle.Observer {
                    callBack.invoke(it ?: arrayListOf())
                })
        }
    }

    fun insertNews(model: NewsSourceModel) {
        handler?.post {
            AppDatabase.appDatabase.customSourcesDao()!!.insertNew(model)
        }
    }

    fun deleteNews(model: NewsSourceModel) {
        handler?.post {
            AppDatabase.appDatabase.customSourcesDao()!!.delete(arrayListOf(model))
        }
    }


    fun update(model: NewsSourceModel) {
        handler?.post {
            AppDatabase.appDatabase.customSourcesDao()!!.update(model)
        }
    }


    fun storeCustomSourcesInDB(context: Context) {
        val dummyData =
            ObjectUtils.readFromAssets(context, "sources.json", NewsSourceModel::class.java)
        handler?.post {
            if (AppDatabase.appDatabase.customSourcesDao()?.getAllItems?.size != 0) return@post
            AppDatabase.appDatabase.customSourcesDao()!!.deleteAll()
            AppDatabase.appDatabase.customSourcesDao()!!.insertAll(dummyData)
        }

    }

    fun getSourceInfo(sourceLink: String, listener: ((sourceInfo: NewsSourceModel?) -> Unit)) {
        handler?.post {
            listener.invoke(AppDatabase.appDatabase.customSourcesDao()!!.getSourceInfo(sourceLink))
        }

    }

}