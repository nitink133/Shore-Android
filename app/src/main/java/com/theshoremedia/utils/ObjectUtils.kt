package com.theshoremedia.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.theshoremedia.database.entity.FactCheckHistoryModel


/**
 * @author- Nitin Khanna
 * @date -
 */


object ObjectUtils {
    inline fun <reified T> clone(
        model: T
    ): T {
        val string = Gson().toJson(model, T::class.java)
        return Gson().fromJson(string, T::class.java)
    }

    inline fun <reified T> clone(
        list: List<T>?
    ): List<T> {
        if (list == null || list.isEmpty()) return arrayListOf()
        val string = toString(list)
        return parseList(string)
    }


    inline fun <reified T> toString(
        list: List<T>?
    ): String {
        if (list == null || list.isEmpty()) return ""
        return Gson().toJson(list)
    }

    inline fun <reified T> toString(
        model: T?
    ): String {
        if (model == null) return ""
        return Gson().toJson(model)
    }

    inline fun <reified T> parseList(
        value: String?
    ): List<T> {
        if (value.isNullOrEmpty()) return arrayListOf()
        val myType = object : TypeToken<List<T>>() {}.type
        return Gson().fromJson(
            value,
            myType
        ) ?: arrayListOf<T>()
    }

    fun parseListTOData(
        value: String?
    ): List<FactCheckHistoryModel> {
        if (value.isNullOrEmpty()) return arrayListOf()
        val myType = object : TypeToken<List<FactCheckHistoryModel>>() {}.type
        return Gson().fromJson(
            value,
            myType
        ) ?: arrayListOf<FactCheckHistoryModel>()
    }

    inline fun <reified T> parseToObject(
        value: String,
        type: Class<T>
    ): T {
        return Gson().fromJson(value, type)
    }


}