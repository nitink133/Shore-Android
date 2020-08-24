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
@Entity(tableName = "tableFactChecksHistory")
data class FactCheckHistoryModel(
    @PrimaryKey
    @ColumnInfo(name = "actualSearchString")
    var actualSearchString: String = "",
    @ColumnInfo(name = "source")
    var source: String = "",
    @ColumnInfo(name = "title")
    var title: String = "",
    @ColumnInfo(name = "description")
    var description: String = "",
    @ColumnInfo(name = "icon")
    var icon: String = "",
    @ColumnInfo(name = "isFavourite")
    var isFavourite: Boolean = false,
    @ColumnInfo(name = "isRead")
    var isRead: Boolean = false,
    @ColumnInfo(name = "date")
    var date: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(actualSearchString)
        parcel.writeString(source)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(icon)
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