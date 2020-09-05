package com.theshoremedia.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * @author- Nitin Khanna
 * @date -
 */
@Entity(tableName = "tableFactChecksHistory")
data class FactCheckHistoryModel(
    @PrimaryKey
    @ColumnInfo(name = "forwardMessage")
    @SerializedName("forwardMessage")
    var forwardMessage: String = "",

    @ColumnInfo(name = "image")
    @SerializedName("image")
    var image: String = "",
    @ColumnInfo(name = "authors")
    @SerializedName("authors")
    var authors: List<String>?,
    @ColumnInfo(name = "articleTitle")
    @SerializedName("articleTitle")
    var articleTitle: String = "",
    @ColumnInfo(name = "articleLink")
    @SerializedName("articleLink")
    var articleLink: String = "",
    @ColumnInfo(name = "article_html")
    @SerializedName("article_html")
    var article_html: String = "",
    @ColumnInfo(name = "article")
    @SerializedName("article")
    var article: String = "",
    @ColumnInfo(name = "sourceName")
    @SerializedName("sourceName")
    var sourceName: String = "",

    @SerializedName("uniqueDevices")
    var uniqueDevices: List<String>?,
    @SerializedName("uniqueRequestCount")
    var uniqueRequestCount: Int?,


    //Local database based
    @ColumnInfo(name = "isFavourite")
    var isFavourite: Boolean = false,
    @ColumnInfo(name = "isRead")
    var isRead: Boolean = false,
    @ColumnInfo(name = "date")
    var date: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(forwardMessage)
        parcel.writeString(image)
        parcel.writeStringList(authors)
        parcel.writeString(articleTitle)
        parcel.writeString(articleLink)
        parcel.writeString(article_html)
        parcel.writeString(article)
        parcel.writeString(sourceName)
        parcel.writeStringList(uniqueDevices)
        parcel.writeValue(uniqueRequestCount)
        parcel.writeByte(if (isFavourite) 1 else 0)
        parcel.writeByte(if (isRead) 1 else 0)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FactCheckHistoryModel> {
        override fun createFromParcel(parcel: Parcel): FactCheckHistoryModel {
            return FactCheckHistoryModel(parcel)
        }

        override fun newArray(size: Int): Array<FactCheckHistoryModel?> {
            return arrayOfNulls(size)
        }
    }
}