package com.theshoremedia.modules.floatingview.credibility_checker.model

import com.google.gson.annotations.SerializedName
import com.theshoremedia.utils.ApplicationUtils

/**
 * @author- Nitin Khanna
 * @date -
 */
data class ValidateNewsReqModel(
    @SerializedName("forwardMessage")
    var query: String,
    @SerializedName("deviceId")
    var deviceId: String? = ApplicationUtils.getUniqueDeviceId(),
    var isProcessing: Boolean = false,
    var isProcessed: Boolean = false
)