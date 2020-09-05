package com.theshoremedia.retrofit.model


import com.google.gson.annotations.SerializedName

data class GenericResponseModel<T>(
    @SerializedName("data")
    var `data`: T?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?
)