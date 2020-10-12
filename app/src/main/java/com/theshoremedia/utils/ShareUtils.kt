package com.theshoremedia.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.hd.viewcapture.ViewCapture
import com.theshoremedia.BuildConfig
import com.theshoremedia.R
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.utils.extensions.getBitmapFromVectorDrawable
import com.theshoremedia.utils.extensions.save
import com.theshoremedia.utils.permissions.StoragePermissionsUtils
import com.watermark.androidwm.WatermarkBuilder
import com.watermark.androidwm.bean.WatermarkImage


/**
 * @author- Nitin Khanna
 * @date - 08-09-2020
 */
object ShareUtils {
    fun takeScreenshotAndShare(view: View?, action: (() -> Unit)) {
        if (view == null) return
        val mContext = view.context
        StoragePermissionsUtils.checkPermission(mContext) {

            val bitmap = ViewCapture.with(view).bitmap
//                if (view is ScrollView) view.getScrollViewScreenShot() ?: return@checkPermission
//                else view.getScreenShot() ?: return@checkPermission


            val logo =
                getBitmapFromVectorDrawable(mContext, R.drawable.ic_shore) ?: return@checkPermission

            val watermarkImage: WatermarkImage = WatermarkImage(logo)
                .setPositionX(0.5)
                .setPositionY(0.5)
                .setRotation(30.0)

            val bitmapNew: Bitmap = WatermarkBuilder
                .create(mContext, bitmap)
                .loadWatermarkImage(watermarkImage)
                .setTileMode(true)
                .watermark
                .outputImage


            val file = bitmapNew.save() ?: return@checkPermission
            val uri: Uri =
                FileProvider.getUriForFile(
                    view.context,
                    "${BuildConfig.APPLICATION_ID}.provider",
                    file
                )

            val articleModel = view.getTag(R.string.key_model) as? FactCheckHistoryModel
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_SUBJECT, "")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                if (articleModel == null) "" else mContext.getString(
                    R.string.read_complete_article_at_and_download_shore_to_detect_fake_news_in_real_time,
                    articleModel.articleLink
                )
            )
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            action.invoke()
            try {
                val chooserIntent = Intent.createChooser(intent, "Share Facts")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ContextCompat.startActivity(view.context, chooserIntent, null)
            } catch (e: ActivityNotFoundException) {
                ToastUtils.makeToast(view.context, "No App Available", Toast.LENGTH_SHORT)
            }
        }
    }

}