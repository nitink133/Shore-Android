package com.theshoremedia.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.TextView
import com.theshoremedia.R
import com.theshoremedia.utils.extensions.makeVisible
import com.theshoremedia.utils.extensions.showCustomDialog
import kotlinx.android.synthetic.main.dialog_lottie.view.*


object DialogUtils {
    private var TAG = DialogUtils::class.java.simpleName
    private var loadingMsg: String = "Loading..."
    private var pDialog: ProgressDialog? = null

    fun showLottieDialog(
        mContext: Activity,
        message: String? = null,
        btnSuccessText: Int = R.string.ok,
        btnFailText: Int = R.string.cancel,
        heading: String? = null,
        isCancelBtnVisible: Boolean = true,
        isCancelable: Boolean = false,
        lottieFile: String = AppConstants.LottieFile.SUCCESS,
        isLottieOnLoop: Boolean = true,
        titleText: String? = null,
        responseListener: ((result: Int) -> Unit)
    ) {
        mContext.let {
            val dialogView =
                it.layoutInflater.inflate(R.layout.dialog_lottie, null)

            var scaleX = 1.0
            var scaleY = 1.0
            if (lottieFile == AppConstants.LottieFile.DELETE) {
                scaleX = 1.8
                scaleY = 1.8
            }
            if (message.isNullOrEmpty()) dialogView.tvMessage?.makeVisible(isVisible = false)
            else dialogView.tvMessage.text = message
            dialogView.lottie.setAnimation(lottieFile)
            dialogView.lottie.loop(isLottieOnLoop)
            dialogView.lottie.scaleX = scaleX.toFloat()
            dialogView.lottie.scaleY = scaleY.toFloat()
            dialogView.btnCancel.text = mContext.getString(btnFailText)
            dialogView.btnOkay.text = mContext.getString(btnSuccessText)

            titleText?.let {
                dialogView.tvTitle.text = it
                dialogView.tvTitle.makeVisible(isVisible = true)
            }

            if (!heading.isNullOrEmpty()) {
                dialogView.tvHeading.makeVisible(isVisible = true)
                dialogView.tvHeading.text = heading.toString()
            }

            dialogView.btnCancel.makeVisible(isVisible = isCancelBtnVisible)

            val customDialog = showCustomDialog(it, dialogView) {
                cancelable = true
            }

            dialogView.btnOkay.setOnClickListener {
                customDialog.dismiss()
                responseListener.invoke(btnSuccessText)
            }
            dialogView.btnCancel.setOnClickListener {
                customDialog.dismiss()
                responseListener.invoke(btnFailText)
            }

            customDialog.setCancelable(isCancelable)
            customDialog.show()
        }
    }


    fun showProgressDialog(context: Context, msg: String? = null) {
        if (pDialog == null) {
            pDialog = ProgressDialog(ContextThemeWrapper(context, R.style.ProgressDialogCustom))
            pDialog!!.setMessage(msg ?: loadingMsg)
            pDialog!!.setCancelable(false)
            pDialog!!.setCanceledOnTouchOutside(false)
        }
        try {
            pDialog!!.show()
        } catch (e: Exception) {
            Log.printStackTrace(e)
        }
    }

    fun dismissProgressDialog() {
        try {
            if (pDialog != null) {
                pDialog!!.dismiss()
                pDialog = null
            }
        } catch (e: Exception) {
            Log.printStackTrace(e)
        }
    }

    fun showDialog(
        context: Context?,
        title: String,
        message: String,
        okayButtonText: Int = R.string.ok,
        cancelButtonText: Int = R.string.cancel,
        hideCancelButton: Boolean = false,
        isCancelable: Boolean = false,
        heading: Int = R.string.app_name,
        responseListener: ((action: Int) -> Unit)? = null
    ) {
        (context as? Activity)?.let {
            val dialogView = it.layoutInflater.inflate(R.layout.dialog_layout, null)
            val msgTxt = dialogView.findViewById(R.id.tvMessage) as TextView
            val tvTitle = dialogView.findViewById(R.id.tvTitle) as TextView
            val tvHeading = dialogView.findViewById(R.id.tvHeading) as TextView
            val btnOkay = dialogView.findViewById(R.id.btnOkay) as Button
            val btnCancel = dialogView.findViewById(R.id.btnCancel) as Button
            msgTxt.text = message
            tvTitle.text = title
            btnOkay.text = context.getString(okayButtonText)
            btnCancel.text = context.getString(cancelButtonText)
            tvHeading.text = context.getString(heading)
            btnCancel.makeVisible(isVisible = !hideCancelButton)
            val customDialog = showCustomDialog(it, dialogView) {
                cancelable = isCancelable
            }
            btnOkay.setOnClickListener {
                responseListener?.invoke(okayButtonText)
                customDialog.dismiss()
            }
            btnCancel.setOnClickListener {
                responseListener?.invoke(cancelButtonText)
                customDialog.dismiss()
            }
            customDialog.show()
        }
    }


}

