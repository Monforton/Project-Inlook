package uab.cs422.projectinlook.ui
// IF CRASH BROUGHT YOU HERE: https://issuetracker.google.com/issues/206855618
import android.content.Context
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

// THIS CLASS COURTESY OF https://stackoverflow.com/a/65607462
open class SwipeListener(c: Context) : OnTouchListener {
    private val gestureDetector: GestureDetectorCompat
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val out = gestureDetector.onTouchEvent(motionEvent)
        if (out) {
            view.performClick()
        }
        return out
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD: Int = 200
        private val SWIPE_VELOCITY_THRESHOLD: Int = 200
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        // IF CRASH BROUGHT YOU HERE: https://issuetracker.google.com/issues/206855618
        @Suppress("NOTHING_TO_OVERRIDE", "ACCIDENTAL_OVERRIDE")
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 != null && e2 != null) {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    }
                }
            }
            return false
        }
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}

    init {
        gestureDetector = GestureDetectorCompat(c, GestureListener())
    }
}