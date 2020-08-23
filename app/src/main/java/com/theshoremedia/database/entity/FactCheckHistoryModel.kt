package com.theshoremedia.database.entity

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
)