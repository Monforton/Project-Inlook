package uab.cs422.projectinlook.ui

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

// THIS CLASS COURTESY OF https://stackoverflow.com/a/65607462
open class SwipeListener(c: Context?) :
    OnTouchListener {
    private val gestureDetector: GestureDetector
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(motionEvent)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD: Int = 200
        private val SWIPE_VELOCITY_THRESHOLD: Int = 200
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean { //TODO this thing will randomly error given null, maybe fix?
            val diffY = e2?.y?.minus((e1?.y ?: 0f)) ?: 0f
            val diffX = e2?.x?.minus((e1?.x ?: 0f)) ?: 0f
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX ?: 0f) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                }
            }
            return false
        }
    }
    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}

    init {
        gestureDetector = GestureDetector(c, GestureListener())
    }
}