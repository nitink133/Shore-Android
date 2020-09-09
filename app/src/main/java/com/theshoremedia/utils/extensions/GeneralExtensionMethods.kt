package com.theshoremedia.utils.extensions

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import com.theshoremedia.modules.base.BaseCustomDialog

/**
 * @author- Nitin Khanna
 * @date -
 */


fun showCustomDialog(
    context: Context,
    view: View,
    func: BaseCustomDialog.() -> Unit
): AlertDialog = BaseCustomDialog(context, view).apply {
    func()
}.create()

fun getScreenSize(): DisplayMetrics {
    return Resources.getSystem().displayMetrics
}

fun dpToPx(dp: Float): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun spToPx(sp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        Resources.getSystem().displayMetrics
    )
}
