package com.theshoremedia.modules.base

import android.app.AlertDialog
import android.content.Context
import android.view.View

/**
 * @author- Nitin Khanna
 * @date -
 */

class BaseCustomDialog(context: Context, view: View) : BaseDialogHelper() {

    override val dialogView: View by lazy {
        view
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)
}