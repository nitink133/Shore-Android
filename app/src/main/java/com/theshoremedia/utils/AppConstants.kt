package com.theshoremedia.utils

import com.theshoremedia.utils.extensions.dpToPx
import kotlin.math.pow

object AppConstants {
    object FragmentConstants {
        const val REPLACE = 0
        const val ADD = 1
    }

    object LottieFile {
        const val SUCCESS = "lottie/no_data.json"
        const val ALERT = "lottie/no_data.json"
        const val DELETE = "lottie/no_data.json"
        const val ERROR = "lottie/no_data.json"
        const val NO_DATA = "lottie/no_data.json"
        const val PERMISSION = "lottie/no_data.json"
    }

    object PermissionsCode {
        const val ACTION_MANAGE_OVERLAY = 1001
        const val ACTION_ACCESSIBILITY = 1002
        const val ACTION_BATTERY_SAVER = 1003
        const val ACTION_STORAGE = 1004
    }

    object FloatingViewType {
        const val ACCESSIBILITY_HELP = 1
        const val ACCESSIBILITY_SUCCESS_DEMO = 2
        const val CREDIBILITY_CHECKER = 3
    }

    object OverlayViewSize {
        val CHAT_HEAD_OUT_OF_SCREEN_X: Int = dpToPx(10f)
        val CHAT_HEAD_SIZE: Int = dpToPx(62f)
        val CHAT_HEAD_PADDING: Int = dpToPx(6f)
        val CHAT_HEAD_EXPANDED_PADDING: Int = dpToPx(4f)
        val CHAT_HEAD_EXPANDED_MARGIN_TOP: Float = dpToPx(6f).toFloat()
        val CLOSE_SIZE = dpToPx(64f)
        val CLOSE_CAPTURE_DISTANCE = dpToPx(100f)
        val CLOSE_ADDITIONAL_SIZE = dpToPx(24f)

        const val CHAT_HEAD_DRAG_TOLERANCE: Float = 20f

        fun distance(x1: Float, x2: Float, y1: Float, y2: Float): Float {
            return ((x1 - x2).pow(2) + (y1 - y2).pow(2))
        }
    }

    object NavigationItem {
        const val HOME = 0
        const val PAST_CHECKS = 1
        const val FAVOURITE = 2
        const val SETTINGS = 3
        const val HELP_SUPPORT = 4
        const val PRIVACY_POLICY = 5
        const val SHARE_APP = 6
        const val RATE_US = 7
        const val ABOUT_US = 8

    }

    object Preference {
        const val IS_FIRST_TIME_USER = "is_first_time_user"
    }

    object Key{
        const val REQUEST_CODE= "request_code"
        const val NOTIFICATION_ID= "notification_id"
    }

    object AccessibilityService{
        const val TRIGGERED_TIME = 1500
        const val STOP_SERVICE = "stop_service"
        const val START_SERVICE = "start_service"
    }

    object ResponseCode{
        const val SUCCESS = 200
    }

    object DummyData{
        const val FORWARD_MESSAGE = "#BigBreaking: Nazma begum(43) sitting in #ShaheenBaghProtest was not feeling well. Doctors found her #coronavirus positive. She denied the treatment and again went to #ShaheenBagh\n" +
                " What kind of moron are these people?\n" +
                "#Coronavirus Reaches Delhi"
        const val DEVICE_ID = "1"
    }
}