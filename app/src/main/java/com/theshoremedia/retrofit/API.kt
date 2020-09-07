package com.theshoremedia.retrofit

import android.content.Context
import com.theshoremedia.modules.base.BaseApiCaller
import com.theshoremedia.modules.floatingview.credibility_checker.model.ValidateNewsReqModel

object API {
    fun callValidateNews(
        mContext: Context?,
        requestBody: ValidateNewsReqModel?,
        listener: ((res: Any) -> Unit)
    ) {
        if (mContext == null || ApiClient.apiService2 == null || requestBody == null) return
        BaseApiCaller.isLoading = false
        BaseApiCaller.execute(
            mContext,
            ApiClient.apiService2!!.validateNews(requestBody),
            listener
        )
    }

}