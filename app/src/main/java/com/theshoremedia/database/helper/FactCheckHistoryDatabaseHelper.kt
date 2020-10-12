package com.theshoremedia.database.helper

import android.content.Context
import android.os.Handler
import androidx.lifecycle.LifecycleOwner
import com.theshoremedia.database.AppDatabase
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.utils.ApplicationUtils
import com.theshoremedia.utils.Log

/**
 * @author- Nitin Khanna
 * @date -
 */
class FactCheckHistoryDatabaseHelper {
    companion object {
        private var databaseManager: FactCheckHistoryDatabaseHelper? = null
        private var handler: Handler? = null
        val instance: FactCheckHistoryDatabaseHelper?
            get() {
                if (databaseManager == null) {
                    databaseManager = FactCheckHistoryDatabaseHelper()
                    handler = Handler()
                }
                return databaseManager
            }
    }


    fun getAllNews(
        owner: LifecycleOwner,
        callBack: ((items: List<FactCheckHistoryModel>) -> Unit)
    ) {
        handler?.post {

            AppDatabase.appDatabase.factChecksHistoryDao()!!.all.observe(
                owner,
                androidx.lifecycle.Observer {
                    callBack.invoke(it ?: arrayListOf())
                })
        }
    }

    fun getAllNews(
        callBack: ((items: List<FactCheckHistoryModel>) -> Unit)
    ) {
        handler?.post {


                callBack.invoke( AppDatabase.appDatabase.factChecksHistoryDao()!!.getAllItems ?: arrayListOf())
        }
    }

    fun getUnreadNews(
        callBack: ((items: List<FactCheckHistoryModel>) -> Unit)
    ) {
        handler?.post {


            callBack.invoke(
                AppDatabase.appDatabase.factChecksHistoryDao()!!.getUnreadNews() ?: arrayListOf()
            )
        }
    }

    fun getFavouriteNews(
        callBack: ((items: List<FactCheckHistoryModel>) -> Unit)
    ) {
        handler?.post {


            callBack.invoke(
                AppDatabase.appDatabase.factChecksHistoryDao()!!.getFavouriteNews()
                    ?: arrayListOf()
            )
        }
    }

    fun getUnreadNewsLive(
        owner: LifecycleOwner,
        callBack: ((items: List<FactCheckHistoryModel>) -> Unit)
    ) {
        handler?.post {
            AppDatabase.appDatabase.factChecksHistoryDao()!!.getUnreadNewsLive().observe(
                owner,
                androidx.lifecycle.Observer {
                    callBack.invoke(it ?: arrayListOf())
                })
        }
    }

    fun getFavouriteNews(
        owner: LifecycleOwner,
        callBack: ((items: List<FactCheckHistoryModel>) -> Unit)
    ) {
        handler?.post {

            AppDatabase.appDatabase.factChecksHistoryDao()!!.getFavouriteNewsLive().observe(
                owner,
                androidx.lifecycle.Observer {
                    callBack.invoke(it ?: arrayListOf())
                })
        }
    }


    fun addDummyData(context: Context) {
        val dummyData = ApplicationUtils.getDummyData(context)
        handler?.post {
            if (AppDatabase.appDatabase.factChecksHistoryDao()?.getAllItems?.size != 0) return@post
            AppDatabase.appDatabase.factChecksHistoryDao()!!.deleteAll()
            AppDatabase.appDatabase.factChecksHistoryDao()!!.insertAll(dummyData)
        }

    }

    fun markAsFav(item: FactCheckHistoryModel) {
        handler?.post {

            AppDatabase.appDatabase.factChecksHistoryDao()?.markedAsFav(item)
        }
    }

    fun markAllAsRead() {
        handler?.post {

            AppDatabase.appDatabase.factChecksHistoryDao()?.markAllAsRead()
        }
    }

    fun updateReadStatus(isRead: Boolean, forwardMessage: String) {
        handler?.post {

            AppDatabase.appDatabase.factChecksHistoryDao()
                ?.updateReadStatus(isRead = isRead, forwardMessage = forwardMessage)
        }
    }

    fun getNews(
        forwardMessage: String,
        callBack: ((item: FactCheckHistoryModel?) -> Unit)
    ) {
        handler?.post {


            callBack.invoke(
                AppDatabase.appDatabase.factChecksHistoryDao()!!.getNews(forwardMessage)
            )
        }
    }

    fun insertNews(model: FactCheckHistoryModel) {
        handler?.post {
            Log.d("Nitin", "insertNews called")
            AppDatabase.appDatabase.factChecksHistoryDao()!!.insertNew(model)
        }
    }

    fun delete(model: FactCheckHistoryModel) {
        handler?.post {
            Log.d("Nitin", "insertNews called")
            AppDatabase.appDatabase.factChecksHistoryDao()!!.delete(arrayListOf(model))
        }
    }

}