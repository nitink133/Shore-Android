package com.theshoremedia.modules.floatingview.credibility_checker.ui


import android.content.Context
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringChain
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.database.helper.FactCheckHistoryDatabaseHelper
import com.theshoremedia.modules.floatingview.credibility_checker.services.CredibilityCheckerService
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.FloatingViewsLayoutParamsUtils
import com.theshoremedia.utils.configs.SpringConfigs
import com.theshoremedia.utils.extensions.dpToPx
import com.theshoremedia.utils.extensions.getScreenSize
import com.theshoremedia.utils.whatsapp.WhatsAppUtils
import com.theshoremedia.views.layouts.Line
import com.theshoremedia.views.layouts.Rectangle
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * @author- Nitin Khanna
 * @date - 25/08/2020
 */

class RootView(context: Context) : View.OnTouchListener, FrameLayout(context) {
    private var articleView = ArticleView(context)
    private var contentView = ContentView(context)
    private var closeView = CloseView(this)
    var bubbleView: BubbleView? = null

    //Variable for handling UI animations
    private var moving = false
    private var toggled = false
    private var motionTrackerUpdated = false
    private var collapsing = false
    private var blockAnim = false
    var wasMoving = false
    var closeCaptured = false
    private var closeVelocityCaptured = false
    private var movingOutOfClose = false
    private var detectedOutOfBounds = false
    private var animatingChatHeadInExpandedView = false
    private var isOnRight = true
        set(value) {
            field = value
            if (bubbleView == null) return
            val lp = bubbleView!!.llUnreadCount.layoutParams as LayoutParams
            lp.gravity = if (value) Gravity.START else Gravity.END
            bubbleView!!.llUnreadCount.layoutParams = lp
        }

    private var initialX = 0.0f
    private var initialY = 0.0f
    private var initialTouchX = 0.0f
    private var initialTouchY = 0.0f
    private var initialVelocityX = 0.0
    private var initialVelocityY = 0.0

    private var horizontalSpringChain: SpringChain? = null
    private var verticalSpringChain: SpringChain? = null

    private var velocityTracker: VelocityTracker? = null
    var motionTracker = LinearLayout(context)

    var showContentRunnable: Runnable? = null
    var showArticleRunnable: Runnable? = null

    private var motionTrackerParams = FloatingViewsLayoutParamsUtils.getDefaultParams(
        width = AppConstants.OverlayViewSize.CHAT_HEAD_SIZE,
        height = AppConstants.OverlayViewSize.CHAT_HEAD_SIZE + 16,
        gravity = Gravity.START or Gravity.TOP,
        flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )

    var params = FloatingViewsLayoutParamsUtils.getDefaultParams(
        width = WindowManager.LayoutParams.MATCH_PARENT,
        height = WindowManager.LayoutParams.MATCH_PARENT,
        flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        gravity = Gravity.START or Gravity.TOP,
        dimAmount = 0.7f
    )

    fun onContentSpringUpdate(
        chatHead: BubbleView,
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

        contentView.x =
            coordinatesX - metrics.widthPixels.toFloat() + 0 * (chatHead.params.width + AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_PADDING) + chatHead.params.width
        contentView.y =
            chatHead.springY.currentValue.toFloat() - AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_MARGIN_TOP
        contentView.pivotX =
            metrics.widthPixels.toFloat() - chatHead.width / 2 - 0 * (chatHead.params.width + AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_PADDING)

        contentView.pivotY = chatHead.height.toFloat()

        val width = dpToPx(100f)
        val height = dpToPx(50f)
        val r1 = Rectangle(
            closeView.x.toDouble() - if (isOnRight) dpToPx(32f) else width,
            closeView.y.toDouble() - height / 2,
            closeView.width.toDouble() + width,
            closeView.height.toDouble() + height
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
            closeView.visibility == VISIBLE &&
            !closeVelocityCaptured
        ) {
            closeVelocityCaptured = true

            chatHead.springX.endValue =
                closeView.springX.endValue + closeView.width / 2 - chatHead.params.width / 2 + 2
            chatHead.springY.endValue =
                closeView.springY.endValue + closeView.height / 2 - chatHead.params.height / 2 + 2

            closeView.enlarge()

            postDelayed({
                CredibilityCheckerService.getInstance().removeBubbleView()
            }, 100)
        }

        if (wasMoving) {
            motionTrackerParams.x = if (isOnRight) metrics.widthPixels - chatHead.width else 0


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

                CredibilityCheckerService.getInstance().updateViewLayout(
                    motionTracker,
                    motionTrackerParams
                )
            }
        }
    }

    /**
     * onTouch event listener for motionTracker
     */
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (bubbleView == null) return true

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchMotionActionDown(event)
            }
            MotionEvent.ACTION_UP -> {
                onTouchMotionActionUp()

            }
            MotionEvent.ACTION_MOVE -> {
                onTouchMotionActionMove(event)
            }
        }

        return true
    }

    private fun onTouchMotionActionMove(event: MotionEvent) {
        if (bubbleView == null) return
        val metrics = getScreenSize()
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
            closeView.springX.endValue =
                (metrics.widthPixels / 2) + (((event.rawX + bubbleView!!.width / 2) / 7) - metrics.widthPixels / 2 / 7) - closeView.width.toDouble() / 2
            closeView.springY.endValue =
                (metrics.heightPixels - AppConstants.OverlayViewSize.CLOSE_SIZE) + max(
                    ((event.rawY + closeView.height / 2) / 10) - metrics.heightPixels / 10,
                    -dpToPx(30f).toFloat()
                ) - dpToPx(60f).toDouble()

            if (AppConstants.OverlayViewSize.distance(
                    closeView.springX.endValue.toFloat() + closeView.width / 2,
                    event.rawX,
                    closeView.springY.endValue.toFloat() + closeView.height / 2,
                    event.rawY
                ) < AppConstants.OverlayViewSize.CLOSE_CAPTURE_DISTANCE.toDouble().pow(2)
            ) {
                bubbleView!!.springX.springConfig =
                    SpringConfigs.CAPTURING
                bubbleView!!.springY.springConfig =
                    SpringConfigs.CAPTURING

                closeView.enlarge()

                closeCaptured = true
            } else if (closeCaptured) {
                bubbleView!!.springX.springConfig =
                    SpringConfigs.CAPTURING
                bubbleView!!.springY.springConfig =
                    SpringConfigs.CAPTURING

                closeView.resetScale()

                bubbleView!!.springX.endValue =
                    initialX + (event.rawX - initialTouchX).toDouble()
                bubbleView!!.springY.endValue =
                    initialY + (event.rawY - initialTouchY).toDouble()

                closeCaptured = false

                movingOutOfClose = true

                postDelayed({
                    movingOutOfClose = false
                }, 100)
            } else if (!movingOutOfClose) {
                bubbleView!!.springX.springConfig =
                    SpringConfigs.DRAGGING
                bubbleView!!.springY.springConfig =
                    SpringConfigs.DRAGGING

                bubbleView!!.springX.currentValue =
                    initialX + (event.rawX - initialTouchX).toDouble()
                bubbleView!!.springY.currentValue =
                    initialY + (event.rawY - initialTouchY).toDouble()

                velocityTracker?.computeCurrentVelocity(2000)
            }
        }

    }

    private fun onTouchMotionActionUp(isArticleView: Boolean = false) {
        val metrics = getScreenSize()
        if (moving) wasMoving = true

        postDelayed({
            closeView.hide()
        }, 100)

        if (closeCaptured) {
            if (CredibilityCheckerService.isInitialized)
                CredibilityCheckerService.getInstance().removeBubbleView()
            return
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

                if (!isArticleView) {
                    showContentRunnable?.let {
                        handler.removeCallbacks(it)
                    }
                    showContentRunnable = Runnable {
                        contentView.showContent()
                    }
                    showContentRunnable?.let {
                        handler.postDelayed(it, 200)
                    }
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
                    ((-bubbleView!!.springX.currentValue - AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X) * SpringConfigs.DRAGGING.friction)
                maxVelocityX = newVelocity - 5000
                if (xVelocity > maxVelocityX)
                    xVelocity = newVelocity - 500
            } else if (xVelocity > 3500) {
                val newVelocity =
                    ((metrics.widthPixels - bubbleView!!.springX.currentValue - bubbleView!!.width + AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X) * SpringConfigs.DRAGGING.friction)
                maxVelocityX = newVelocity + 5000
                if (maxVelocityX > xVelocity)
                    xVelocity = newVelocity + 500
            } else if (yVelocity > 20 || yVelocity < -20) {
                bubbleView!!.springX.springConfig =
                    SpringConfigs.NOT_DRAGGING

                if (bubbleView!!.x >= metrics.widthPixels / 2) {
                    bubbleView!!.springX.endValue =
                        metrics.widthPixels - bubbleView!!.width + AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                    isOnRight = true
                } else {
                    bubbleView!!.springX.endValue =
                        -AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()

                    isOnRight = false
                }
            } else {
                bubbleView!!.springX.springConfig =
                    SpringConfigs.NOT_DRAGGING
                bubbleView!!.springY.springConfig =
                    SpringConfigs.NOT_DRAGGING

                if (bubbleView!!.x >= metrics.widthPixels / 2) {
                    bubbleView!!.springX.endValue =
                        metrics.widthPixels - bubbleView!!.width +
                                AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                    bubbleView!!.springY.endValue = bubbleView!!.y.toDouble()

                    isOnRight = true
                } else {
                    bubbleView!!.springX.endValue =
                        -AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                    bubbleView!!.springY.endValue = bubbleView!!.y.toDouble()

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

            bubbleView!!.springX.velocity = xVelocity
            bubbleView!!.springY.velocity = yVelocity
        }

        bubbleView!!.scaleX = 1f
        bubbleView!!.scaleY = 1f

    }

    private fun onTouchMotionActionDown(event: MotionEvent) {
        if (bubbleView == null) return
        initialX = bubbleView!!.springX.currentValue.toFloat()
        initialY = bubbleView!!.springY.currentValue.toFloat()
        initialTouchX = event.rawX
        initialTouchY = event.rawY

        wasMoving = false
        collapsing = false
        blockAnim = false
        detectedOutOfBounds = false
        closeVelocityCaptured = false

        closeView.show()

        bubbleView!!.scaleX = 0.92f
        bubbleView!!.scaleY = 0.92f

        bubbleView!!.springX.springConfig =
            SpringConfigs.DRAGGING
        bubbleView!!.springY.springConfig =
            SpringConfigs.DRAGGING

        bubbleView!!.springX.setAtRest()
        bubbleView!!.springY.setAtRest()

        motionTrackerUpdated = false

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        } else {
            velocityTracker?.clear()
        }

        velocityTracker?.addMovement(event)

    }

    private fun rearrangeExpanded(animation: Boolean = true) {
        val metrics = getScreenSize()

        bubbleView!!.springX.springConfig =
            SpringConfigs.NOT_DRAGGING
        bubbleView!!.springY.springConfig =
            SpringConfigs.NOT_DRAGGING

        val x =
            metrics.widthPixels - bubbleView!!.params.width.toDouble() - 0 * (bubbleView!!.params.width + AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_PADDING).toDouble()
        val y = AppConstants.OverlayViewSize.CHAT_HEAD_EXPANDED_MARGIN_TOP.toDouble()

        if (animation) {
            bubbleView!!.springY.endValue = y
            bubbleView!!.springX.endValue = x
        } else {
            bubbleView!!.springY.currentValue = y
            bubbleView!!.springX.currentValue = x
        }
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

    fun collapse() {
        toggled = false
        collapsing = true

        val metrics = getScreenSize()

        if (bubbleView != null) {
            val newX =
                if (isOnRight) metrics.widthPixels - bubbleView!!.width + AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                else -AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
            val newY = initialY.toDouble()

            bubbleView!!.springX.endValue = newX
            bubbleView!!.springY.endValue = newY
        }


        hideContentView()

        motionTrackerParams.flags =
            motionTrackerParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
        CredibilityCheckerService.getInstance().updateViewLayout(motionTracker, motionTrackerParams)

        params.flags = (params.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) and
                WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv() and
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL.inv() or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

        CredibilityCheckerService.getInstance().updateViewLayout(this, params)

    }

    fun makeContentViewEnable() {
        contentView.isDisable = false
    }


    init {
        CredibilityCheckerService.getInstance().addView(motionTracker, motionTrackerParams)
        addView(contentView)
        addView(articleView)

        motionTracker.setOnTouchListener(this)
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

    fun addShoreBubble() {
        val metrics = getScreenSize()
        val lx =
            metrics.widthPixels - AppConstants.OverlayViewSize.CHAT_HEAD_SIZE - 16 + AppConstants.OverlayViewSize.CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
        val ly = 0.0
        if (bubbleView == null) {
            bubbleView = BubbleView(this)
        }
        if (!moving) blockAnim = true

        bubbleView!!.springX.currentValue = lx
        bubbleView!!.springY.currentValue = ly


        motionTrackerParams.x = bubbleView!!.springX.currentValue.toInt()
        motionTrackerParams.y = bubbleView!!.springY.currentValue.toInt()
        motionTrackerParams.flags =
            motionTrackerParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
        CredibilityCheckerService.getInstance().updateViewLayout(
            motionTracker,
            motionTrackerParams
        )
    }

    fun onClose() {
        collapse()
        destroySpringChains()
        removeAllViews()
        bubbleView = null
        closeCaptured = false
        movingOutOfClose = false


        FactCheckHistoryDatabaseHelper.instance?.markAllAsRead()
        WhatsAppUtils.getInstance()?.forwardedMessagesList?.clear()
    }

    fun showContentView() {
        contentView.showContent()
    }

    fun hideContentView() {
        if (articleView.isVisible) {
            articleView.hide()
        }
        contentView.hideContent()
    }

    fun showArticleView(item: FactCheckHistoryModel) {

        contentView.isDisable = true

        onTouchMotionActionUp(true)
        showArticleRunnable?.let {
            handler.removeCallbacks(it)
        }

        showArticleRunnable = Runnable {
            articleView.show(item)
        }
        showArticleRunnable?.let {
            handler.postDelayed(it, 200)
        }
    }

    fun refreshData() {
        contentView.updateAdapterWithUnreadNews()
    }

}
