package com.theshoremedia.floatingview.credibility.ui

/**
 * @author- Nitin Khanna
 * @date -
 */

import android.content.Context
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringChain
import com.theshoremedia.floatingview.credibility.services.CredibilityCheckerService
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.FloatingViewsLayoutParamsUtils
import com.theshoremedia.utils.configs.SpringConfigs
import com.theshoremedia.utils.extensions.dpToPx
import com.theshoremedia.utils.extensions.getScreenSize
import com.theshoremedia.views.layouts.Line
import com.theshoremedia.views.layouts.Rectangle
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class CredibilityCheckerRootView(context: Context) : View.OnTouchListener, FrameLayout(context) {


    private var content =
        CredibilityCheckerContentView(
            context
        )
    private var close =
        CredibilityCheckerCloseView(
            this
        )

    var chatHead: CredibilityCheckerBubbleView? = null

    var wasMoving = false
    var closeCaptured = false

    private var closeVelocityCaptured = false

    private var movingOutOfClose = false

    private var initialX = 0.0f
    private var initialY = 0.0f

    private var initialTouchX = 0.0f
    private var initialTouchY = 0.0f

    var initialVelocityX = 0.0
    var initialVelocityY = 0.0

    private var lastY = 0.0

    private var moving = false
    private var toggled = false
    private var motionTrackerUpdated = false
    private var collapsing = false
    private var blockAnim = false

    private var horizontalSpringChain: SpringChain? = null
    private var verticalSpringChain: SpringChain? = null

    private var isOnRight = true
        set(value) {
            field = value

            val lp = chatHead!!.llUnreadCount.layoutParams as LayoutParams
            lp.gravity = if (value) Gravity.START else Gravity.END
            chatHead!!.llUnreadCount.layoutParams = lp
        }

    private var velocityTracker: VelocityTracker? = null

    private var motionTracker = LinearLayout(context)

    private var detectedOutOfBounds = false

    private var animatingChatHeadInExpandedView = false

    var showContentRunnable: Runnable? = null

    private var motionTrackerParams = FloatingViewsLayoutParamsUtils.getDefaultParams(
        width = AppConstants.OverlayViewSize.CHAT_HEAD_SIZE,
        height = AppConstants.OverlayViewSize.CHAT_HEAD_SIZE + 16,
        gravity = Gravity.START or Gravity.TOP,
        flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )

    private var params = FloatingViewsLayoutParamsUtils.getDefaultParams(
        width = WindowManager.LayoutParams.MATCH_PARENT,
        height = WindowManager.LayoutParams.MATCH_PARENT,
        flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        gravity = Gravity.START or Gravity.TOP,
        dimAmount = 0.7f

    )

    init {
        CredibilityCheckerService.getInstance().windowManager.addView(
            motionTracker,
            motionTrackerParams
        )
        CredibilityCheckerService.getInstance().windowManager.addView(this, params)
        this.addView(content)

        isFocusableInTouchMode = true

        motionTracker.setOnTouchListener(this)
        addShoreBubble()
        setOnTouchListener { v, event ->
            v.performClick()

            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (v == this) {
                        collapse()
                    }
                }
            }

            return@setOnTouchListener false
        }
    }

    private fun addShoreBubble() {
        val metrics = getScreenSize()

        val lx =
            metrics.widthPixels - AppConstants.OverlayViewSize.CHAT_HEAD_SIZE - 16 + AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
        val ly = 0.0

        if (chatHead == null) {
            chatHead =
                CredibilityCheckerBubbleView(
                    this
                )
        }

        if (!moving) blockAnim = true

        chatHead!!.springX.currentValue = lx
        chatHead!!.springY.currentValue = ly


        motionTrackerParams.x = chatHead!!.springX.currentValue.toInt()
        motionTrackerParams.y = chatHead!!.springY.currentValue.toInt()
        motionTrackerParams.flags =
            motionTrackerParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()

        CredibilityCheckerService.getInstance().windowManager.updateViewLayout(
            motionTracker,
            motionTrackerParams
        )

    }


    fun onChatHeadSpringUpdate(
        chatHead: CredibilityCheckerBubbleView,
        spring: Spring,
        totalVelocity: Int
    ) {
        val metrics = getScreenSize()

        if (horizontalSpringChain != null && spring == chatHead.springX) {
            horizontalSpringChain!!.controlSpring.currentValue = spring.currentValue
        }

        if (verticalSpringChain != null && spring == chatHead.springY) {
            verticalSpringChain!!.controlSpring.currentValue = spring.currentValue
        }


        // Whether the content should follow chat head x
        val coordinatesX =
            (if (animatingChatHeadInExpandedView) chatHead.springX.endValue else chatHead.springX.currentValue).toFloat()

        content.x =
            coordinatesX - metrics.widthPixels.toFloat() + 0 * (chatHead.params.width + AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_PADDING) + chatHead.params.width
        content.y =
            chatHead.springY.currentValue.toFloat() - AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_MARGIN_TOP
        content.pivotX =
            metrics.widthPixels.toFloat() - chatHead.width / 2 - 0 * (chatHead.params.width + AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_PADDING)

        content.pivotY = chatHead.height.toFloat()

        val width = dpToPx(100f)
        val height = dpToPx(50f)
        val r1 = Rectangle(
            close.x.toDouble() - if (isOnRight) dpToPx(32f) else width,
            close.y.toDouble() - height / 2,
            close.width.toDouble() + width,
            close.height.toDouble() + height
        )

        val x = chatHead.springX.currentValue + chatHead.params.width / 2
        val y = chatHead.springY.currentValue + chatHead.params.height / 2
        val l1 = Line(
            x,
            y,
            initialVelocityX + x,
            initialVelocityY + y
        )

        // When a chat head has been thrown towards close
        if (
            !moving &&
            initialVelocityY > 5000.0 &&
            l1.intersects(r1) &&
            close.visibility == VISIBLE &&
            !closeVelocityCaptured
        ) {
            closeVelocityCaptured = true

            chatHead.springX.endValue =
                close.springX.endValue + close.width / 2 - chatHead.params.width / 2 + 2
            chatHead.springY.endValue =
                close.springY.endValue + close.height / 2 - chatHead.params.height / 2 + 2

            close.enlarge()

            postDelayed({
                onClose()
            }, 100)
        }

        if (wasMoving) {
            motionTrackerParams.x = if (isOnRight) metrics.widthPixels - chatHead.width else 0

            lastY = chatHead.springY.currentValue

            if (!detectedOutOfBounds && !closeCaptured && !closeVelocityCaptured) {
                if (chatHead.springY.currentValue < 0) {
                    chatHead.springY.endValue = 0.0
                    detectedOutOfBounds = true
                } else if (chatHead.springY.currentValue > metrics.heightPixels) {
                    chatHead.springY.endValue =
                        metrics.heightPixels - AppConstants.OverlayViewSize.CHAT_HEAD_SIZE.toDouble()
                    detectedOutOfBounds = true
                }
            }

            if (!moving) {
                if (spring === chatHead.springX) {
                    val xPosition = chatHead.springX.currentValue
                    if (xPosition + chatHead.width > metrics.widthPixels && chatHead.springX.velocity > 0) {
                        val newPos =
                            metrics.widthPixels - chatHead.width + AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X
                        chatHead.springX.springConfig =
                            SpringConfigs.NOT_DRAGGING
                        chatHead.springX.endValue = newPos.toDouble()
                        isOnRight = true
                    } else if (xPosition < 0 && chatHead.springX.velocity < 0) {
                        chatHead.springX.springConfig =
                            SpringConfigs.NOT_DRAGGING
                        chatHead.springX.endValue =
                            -AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                        isOnRight = false
                    }
                } else if (spring === chatHead.springY) {
                    val yPosition = chatHead.springY.currentValue
                    if (yPosition + chatHead.height > metrics.heightPixels && chatHead.springY.velocity > 0) {
                        chatHead.springY.springConfig =
                            SpringConfigs.NOT_DRAGGING
                        chatHead.springY.endValue =
                            metrics.heightPixels - chatHead.height.toDouble() - dpToPx(25f)
                    } else if (yPosition < 0 && chatHead.springY.velocity < 0) {
                        chatHead.springY.springConfig =
                            SpringConfigs.NOT_DRAGGING
                        chatHead.springY.endValue = 0.0
                    }
                }
            }

            if (Math.abs(totalVelocity) % 10 == 0 && !moving) {
                motionTrackerParams.y = chatHead.springY.currentValue.toInt()

                CredibilityCheckerService.getInstance().windowManager.updateViewLayout(
                    motionTracker,
                    motionTrackerParams
                )
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val metrics = getScreenSize()

        if (chatHead == null) return true

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = chatHead!!.springX.currentValue.toFloat()
                initialY = chatHead!!.springY.currentValue.toFloat()
                initialTouchX = event.rawX
                initialTouchY = event.rawY

                wasMoving = false
                collapsing = false
                blockAnim = false
                detectedOutOfBounds = false
                closeVelocityCaptured = false

                close.show()

                chatHead!!.scaleX = 0.92f
                chatHead!!.scaleY = 0.92f

                chatHead!!.springX.springConfig =
                    SpringConfigs.DRAGGING
                chatHead!!.springY.springConfig =
                    SpringConfigs.DRAGGING

                chatHead!!.springX.setAtRest()
                chatHead!!.springY.setAtRest()

                motionTrackerUpdated = false

                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain()
                } else {
                    velocityTracker?.clear()
                }

                velocityTracker?.addMovement(event)
            }
            MotionEvent.ACTION_UP -> {
                if (moving) wasMoving = true

                postDelayed({
                    close.hide()
                }, 100)

                if (closeCaptured) {
                    onClose()
                    return true
                }

                if (!moving) {
                    if (!toggled) {
                        toggled = true

                        rearrangeExpanded()

                        motionTrackerParams.flags =
                            motionTrackerParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        CredibilityCheckerService.getInstance().updateViewLayout(
                            motionTracker,
                            motionTrackerParams
                        )

                        params.flags =
                            (params.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()) or WindowManager.LayoutParams.FLAG_DIM_BEHIND and WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL.inv() and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
                        CredibilityCheckerService.getInstance().updateViewLayout(this, params)

                        showContentRunnable?.let {
                            handler.removeCallbacks(it)
                        }

                        showContentRunnable = Runnable {
                            content.showContent()
                        }
                        showContentRunnable?.let {
                            handler.postDelayed(it, 200)
                        }
                    }
                } else if (!toggled) {
                    moving = false

                    var xVelocity = velocityTracker!!.xVelocity.toDouble()
                    val yVelocity = velocityTracker!!.yVelocity.toDouble()
                    var maxVelocityX = 0.0

                    velocityTracker?.recycle()
                    velocityTracker = null

                    if (xVelocity < -3500) {
                        val newVelocity =
                            ((-chatHead!!.springX.currentValue - AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X) * SpringConfigs.DRAGGING.friction)
                        maxVelocityX = newVelocity - 5000
                        if (xVelocity > maxVelocityX)
                            xVelocity = newVelocity - 500
                    } else if (xVelocity > 3500) {
                        val newVelocity =
                            ((metrics.widthPixels - chatHead!!.springX.currentValue - chatHead!!.width + AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X) * SpringConfigs.DRAGGING.friction)
                        maxVelocityX = newVelocity + 5000
                        if (maxVelocityX > xVelocity)
                            xVelocity = newVelocity + 500
                    } else if (yVelocity > 20 || yVelocity < -20) {
                        chatHead!!.springX.springConfig =
                            SpringConfigs.NOT_DRAGGING

                        if (chatHead!!.x >= metrics.widthPixels / 2) {
                            chatHead!!.springX.endValue =
                                metrics.widthPixels - chatHead!!.width + AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                            isOnRight = true
                        } else {
                            chatHead!!.springX.endValue =
                                -AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()

                            isOnRight = false
                        }
                    } else {
                        chatHead!!.springX.springConfig =
                            SpringConfigs.NOT_DRAGGING
                        chatHead!!.springY.springConfig =
                            SpringConfigs.NOT_DRAGGING

                        if (chatHead!!.x >= metrics.widthPixels / 2) {
                            chatHead!!.springX.endValue =
                                metrics.widthPixels - chatHead!!.width +
                                        AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                            chatHead!!.springY.endValue = chatHead!!.y.toDouble()

                            isOnRight = true
                        } else {
                            chatHead!!.springX.endValue =
                                -AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                            chatHead!!.springY.endValue = chatHead!!.y.toDouble()

                            isOnRight = false
                        }
                    }

                    xVelocity = if (xVelocity < 0) {
                        max(xVelocity - 1000.0, maxVelocityX)
                    } else {
                        min(xVelocity + 1000.0, maxVelocityX)
                    }

                    initialVelocityX = xVelocity
                    initialVelocityY = yVelocity

                    chatHead!!.springX.velocity = xVelocity
                    chatHead!!.springY.velocity = yVelocity
                }

                chatHead!!.scaleX = 1f
                chatHead!!.scaleY = 1f
            }
            MotionEvent.ACTION_MOVE -> {
                if (AppConstants.OverlayViewSize.distance(
                        initialTouchX,
                        event.rawX,
                        initialTouchY,
                        event.rawY
                    ) > AppConstants.OverlayViewSize.CHAT_HEAD_DRAG_TOLERANCE.pow(2)
                ) {
                    moving = true
                }

                velocityTracker?.addMovement(event)

                if (moving) {
                    close.springX.endValue =
                        (metrics.widthPixels / 2) + (((event.rawX + chatHead!!.width / 2) / 7) - metrics.widthPixels / 2 / 7) - close.width.toDouble() / 2
                    close.springY.endValue =
                        (metrics.heightPixels - AppConstants.OverlayViewSize.CLOSE_SIZE) + max(
                            ((event.rawY + close.height / 2) / 10) - metrics.heightPixels / 10,
                            -dpToPx(30f).toFloat()
                        ) - dpToPx(60f).toDouble()

                    if (AppConstants.OverlayViewSize.distance(
                            close.springX.endValue.toFloat() + close.width / 2,
                            event.rawX,
                            close.springY.endValue.toFloat() + close.height / 2,
                            event.rawY
                        ) < AppConstants.OverlayViewSize.CLOSE_CAPTURE_DISTANCE.toDouble().pow(2)
                    ) {
                        chatHead!!.springX.springConfig =
                            SpringConfigs.CAPTURING
                        chatHead!!.springY.springConfig =
                            SpringConfigs.CAPTURING

                        close.enlarge()

                        closeCaptured = true
                    } else if (closeCaptured) {
                        chatHead!!.springX.springConfig =
                            SpringConfigs.CAPTURING
                        chatHead!!.springY.springConfig =
                            SpringConfigs.CAPTURING

                        close.resetScale()

                        chatHead!!.springX.endValue =
                            initialX + (event.rawX - initialTouchX).toDouble()
                        chatHead!!.springY.endValue =
                            initialY + (event.rawY - initialTouchY).toDouble()

                        closeCaptured = false

                        movingOutOfClose = true

                        postDelayed({
                            movingOutOfClose = false
                        }, 100)
                    } else if (!movingOutOfClose) {
                        chatHead!!.springX.springConfig =
                            SpringConfigs.DRAGGING
                        chatHead!!.springY.springConfig =
                            SpringConfigs.DRAGGING

                        chatHead!!.springX.currentValue =
                            initialX + (event.rawX - initialTouchX).toDouble()
                        chatHead!!.springY.currentValue =
                            initialY + (event.rawY - initialTouchY).toDouble()

                        velocityTracker?.computeCurrentVelocity(2000)
                    }
                }
            }
        }

        return true
    }

    private fun rearrangeExpanded(animation: Boolean = true) {
        val metrics = getScreenSize()

        chatHead!!.springX.springConfig =
            SpringConfigs.NOT_DRAGGING
        chatHead!!.springY.springConfig =
            SpringConfigs.NOT_DRAGGING

        val x =
            metrics.widthPixels - chatHead!!.params.width.toDouble() - 0 * (chatHead!!.params.width + AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_PADDING).toDouble()
        val y = AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_MARGIN_TOP.toDouble()

        if (animation) {
            chatHead!!.springY.endValue = y
            chatHead!!.springX.endValue = x
        } else {
            chatHead!!.springY.currentValue = y
            chatHead!!.springX.currentValue = x
        }
    }

    fun collapse() {
        toggled = false
        collapsing = true

        val metrics = getScreenSize()

        if (chatHead != null) {
            val newX =
                if (isOnRight) metrics.widthPixels - chatHead!!.width + AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                else -AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
            val newY = initialY.toDouble()

            chatHead!!.springX.endValue = newX
            chatHead!!.springY.endValue = newY
        }


        content.hideContent()

        motionTrackerParams.flags =
            motionTrackerParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
        CredibilityCheckerService.getInstance().updateViewLayout(motionTracker, motionTrackerParams)

        params.flags = (params.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) and
                WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv() and
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL.inv() or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

        CredibilityCheckerService.getInstance().updateViewLayout(this, params)
    }

    private fun onClose() {
        destroySpringChains()
        this.removeView(chatHead)
        chatHead = null
        closeCaptured = false
        movingOutOfClose = false
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            collapse()
            true
        } else super.dispatchKeyEvent(event)
    }

    private fun destroySpringChains() {
        if (horizontalSpringChain != null) {
            for (spring in horizontalSpringChain!!.allSprings) {
                spring.destroy()
            }
        }

        if (verticalSpringChain != null) {
            for (spring in verticalSpringChain!!.allSprings) {
                spring.destroy()
            }
        }

        verticalSpringChain = null
        horizontalSpringChain = null
    }

    fun showContentView() {
        content.showContent()
    }

    fun hideContentView() {
        content.hideContent()
    }

}
