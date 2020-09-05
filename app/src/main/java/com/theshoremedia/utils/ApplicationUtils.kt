package com.theshoremedia.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.theshoremedia.BuildConfig
import com.theshoremedia.database.entity.FactCheckHistoryModel
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*


/**
 * @author- Nitin Khanna
 * @date -
 */
object ApplicationUtils {
    fun startApp(mContext: Context?) {
        if (mContext == null) return
        val packageName = BuildConfig.APPLICATION_ID
        var intent: Intent? = mContext.packageManager.getLaunchIntentForPackage(packageName)

        if (intent == null) {
            intent = try {
                // if play store installed, open play store, else open browser
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            } catch (e: Exception) {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            }
        }
        mContext.startActivity(intent)
    }

    fun getDummyData(context: Context): List<FactCheckHistoryModel> {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        json = try {
            val `is`: InputStream = context.assets.open("dummy_data.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return arrayListOf()
        }
        return ObjectUtils.parseListTOData(json)
    }

     fun getUniqueDeviceId():String {
        val uniquePseudoID =
            "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10
        val serial = Build.getRadioVersion()
        val uuid: String =
            UUID(uniquePseudoID.hashCode().toLong(), serial.hashCode().toLong()).toString()
        val brand = Build.BRAND
        val modelno = Build.MODEL
        val version = Build.VERSION.RELEASE
        val deviceId =
            "Device Info: \nuuid is : $uuid \nbrand is: $brand \nmodel is: $modelno \nversion is: $version"
        Log.d(
            message = deviceId
        )
        return deviceId
    }
}
