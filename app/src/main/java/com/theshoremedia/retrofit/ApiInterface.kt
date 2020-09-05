package com.theshoremedia.retrofit

import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.modules.floatingview.credibility_checker.model.ValidateNewsReqModel
import com.theshoremedia.retrofit.model.GenericResponseModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {
    @FormUrlEncoded
    @POST("/validateFact")
    fun validateNews(@Field("query") query: String,@Field("deviceId")deviceId:String): Observable<GenericResponseModel<FactCheckHistoryModel?>?>
}