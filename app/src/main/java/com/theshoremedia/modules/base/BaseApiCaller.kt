package com.theshoremedia.modules.base

import android.app.ProgressDialog
import android.content.Context
import android.view.Window
import com.theshoremedia.R
import com.theshoremedia.retrofit.NetworkManager.isNetworkEnable
import com.theshoremedia.retrofit.model.GenericResponseModel
import com.theshoremedia.utils.Log
import com.theshoremedia.utils.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

object BaseApiCaller {
    private var pDialog: ProgressDialog? = null
     var isLoading = true
    private val compositeDisposable = CompositeDisposable()
    fun <E> execute(
        mContext: Context,
        observable: Observable<E>,
        responseListener: ((responseModel: Any) -> Unit)
    ) {
        if (isNetworkEnable) {
            if (isLoading) {
                showProgressDialog(mContext)
            }
            compositeDisposable.add(observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    { genericResponseModel: E ->
                        dismissProgressDialog()
                        if (genericResponseModel is GenericResponseModel<*>) {
                            handleServerResponse(
                                genericResponseModel as GenericResponseModel<*>,
                                responseListener
                            )
                        }
                    }
                ) { throwable: Throwable ->
                    dismissProgressDialog()
                    ToastUtils.makeToast(mContext, throwable.message.toString())
                    Log.e(message = throwable.message.toString())
                }
            )
        } else {
            ToastUtils.makeToast(
                mContext,
                mContext.getString(R.string.exception_message_no_connection)
            )
        }
    }



    private fun showProgressDialog(context: Context) {
        dismissProgressDialog()
        pDialog = ProgressDialog(context)
        pDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pDialog!!.setMessage("Please wait...")
        pDialog!!.setCancelable(false)
        pDialog!!.show()
    }

    private fun dismissProgressDialog() {
        if (pDialog != null) {
            pDialog!!.dismiss()
            pDialog = null
        }
    }




    private fun handleServerResponse(
        response: GenericResponseModel<*>,
        responseListener: ((responseModel: Any) -> Unit)
    ) {
        responseListener.invoke(response)
    }
}