package com.theshoremedia.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


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

    fun <T> parseListTOData(jsonString: String, clazz: Class<T>): List<T> {
        val gson = Gson()
        val objects = gson.fromJson(jsonString, JsonElement::class.java).asJsonArray
        return objects.map { gson.fromJson(it, clazz) }
    }

    inline fun <reified T> parseToObject(
        value: String,
        type: Class<T>
    ): T {
        return Gson().fromJson(value, type)
    }


    inline fun <reified T> readFromAssets(
        mContext: Context,
        fileName: String,
        clazz: Class<T>
    ): List<T> {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        json = try {
            val `is`: InputStream = mContext.assets.open(fileName)
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return arrayListOf()
        }
        return parseListTOData(json, clazz)
    }
}