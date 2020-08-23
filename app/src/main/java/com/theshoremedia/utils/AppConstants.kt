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
        const val BOOKMARK = 1
        const val SETTINGS = 2
        const val HELP_SUPPORT = 3
        const val ABOUT_US = 4
        const val PRIVACY_POLICY = 5

    }

    object Preference {
        const val IS_FIRST_TIME_USER = "is_first_time_user"
    }

}