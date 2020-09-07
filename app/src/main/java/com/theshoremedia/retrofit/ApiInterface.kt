package com.theshoremedia.retrofit

import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.modules.floatingview.credibility_checker.model.ValidateNewsReqModel
import com.theshoremedia.retrofit.model.GenericResponseModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("/validateFact")
    fun validateNews(@Body reqModel: ValidateNewsReqModel): Observable<GenericResponseModel<FactCheckHistoryModel?>?>
}