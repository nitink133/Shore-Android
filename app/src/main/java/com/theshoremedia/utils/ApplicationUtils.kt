package com.theshoremedia.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.theshoremedia.BuildConfig
import com.theshoremedia.R
import com.theshoremedia.database.entity.FactCheckHistoryModel
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*
import kotlin.system.exitProcess


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
        return ObjectUtils.parseListTOData(json, FactCheckHistoryModel::class.java)
    }

    fun getUniqueDeviceId(): String {
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


    fun openAppOnPlayStore(mContext: Context?) {
        if (mContext == null) return
        val uri: Uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            mContext.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            mContext.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                )
            )
        }
    }

    fun mailToSupport(mContext: Context?) {
        if (mContext == null) return
        val intent = Intent(Intent.ACTION_SENDTO) // it's not ACTION_SEND
        intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.support_mail_subject))
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.support_mail_text))
        intent.data =
            Uri.parse("mailto:${mContext.getString(R.string.support_email)}") // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        mContext.startActivity(intent)
    }

    fun shareApp(context: Context) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Download Shore app to get rid of fake news on WhatsApp: https://bit.ly/35UfDCz"
        )
        sendIntent.type = "text/plain"
        context.startActivity(sendIntent)
    }

    fun restartApplication(mContext: Context?) {
        if(mContext == null )return
        if(mContext !is Activity)return
        // Systems at 29/Q and later don't allow relaunch, but System.exit(0) on
        // all supported systems will relaunch ... but by killing the process, then
        // restarting the process with the back stack intact. We must make sure that
        // the launch activity is the only thing in the back stack before exiting.
        val pm = mContext.packageManager
        val intent = pm.getLaunchIntentForPackage(mContext.packageName)
        mContext.finishAffinity() // Finishes all activities.
        mContext.startActivity(intent) // Start the launch activity
        exitProcess(0) // System finishes and automatically relaunches us.
    }
}
