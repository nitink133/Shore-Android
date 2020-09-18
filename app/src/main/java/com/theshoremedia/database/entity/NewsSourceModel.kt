package com.theshoremedia.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author- Nitin Khanna
 * @date -
 */
@Entity(tableName = "customNewsSources")
data class NewsSourceModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int,
    @ColumnInfo(name = "_name")
    var name: String,
    @ColumnInfo(name = "_website")
    var website: String,
    @ColumnInfo(name = "_logo")
    var logo: String = "",
    @ColumnInfo(name = "_about")
    var about: String = "",
    @ColumnInfo(name = "_ifcnSignatory")
    var ifcnSignatory: String? = null,
    @ColumnInfo(name = "_ifcnLogo")
    var ifcnLogo: String? = null,
    @ColumnInfo(name = "_isSelected")
    var isSelected: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(website)
        parcel.writeString(logo)
        parcel.writeString(about)
        parcel.writeString(ifcnSignatory)
        parcel.writeString(ifcnLogo)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewsSourceModel> {
        override fun createFromParcel(parcel: Parcel): NewsSourceModel {
            return NewsSourceModel(parcel)
        }

        override fun newArray(size: Int): Array<NewsSourceModel?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return name
    }

}