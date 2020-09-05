package com.theshoremedia.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.theshoremedia.R
import com.theshoremedia.modules.floatingview.credibility_checker.model.ValidateNewsReqModel
import com.theshoremedia.retrofit.API
import com.theshoremedia.utils.ObjectUtils
import com.theshoremedia.utils.whatsapp.WhatsAppUtils
import kotlinx.android.synthetic.main.activity_shore_checks.*


class ShoreChecksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shore_checks)
//        val text = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
//        finish()

//        WhatsAppUtils.getInstance(this).debugView()
//        finish()

//        API.callValidateNews(
//            this, ValidateNewsReqModel(
//                query = "#BigBreaking: Nazma begum(43) sitting in #ShaheenBaghProtest was not feeling well. Doctors found her #coronavirus positive. She denied the treatment and again went to #ShaheenBagh\n" +
//                        " What kind of moron are these people?\n" +
//                        "#Coronavirus Reaches Delhi", deviceId = "1"
//            )
//        ) {
//            textView?.text = ObjectUtils.toString(it)
//        }
    }
}
