package com.theshoremedia.modules.factchecks.model

/**
 * @author- Nitin Khanna
 * @date -
 */
data class NewsModel(
    var source: String = "",
    var title: String = "",
    var description: String = "",
    var icon: String = "",
    var isSaved: Boolean = false,
    var data: String = ""
)